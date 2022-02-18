package com.github.manzurola.languagetoys.modules.grammar;

import com.github.manzurola.aligner.edit.Operation;
import com.github.manzurola.errant4j.core.Annotation;
import com.github.manzurola.errant4j.core.Annotator;
import com.github.manzurola.spacy4j.api.containers.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GrammarAssessmentService {
    private static final Logger logger = LoggerFactory.getLogger(
        GrammarAssessmentService.class);

    private final Annotator annotator;
    private final PenaltyScoringStrategy scoringStrategy;

    public GrammarAssessmentService(
        Annotator annotator,
        PenaltyScoringStrategy scoringStrategy
    ) {
        this.annotator = annotator;
        this.scoringStrategy = scoringStrategy;
    }

    public GrammarAssessmentResponse assess(GrammarAssessmentRequest request) {
        logger.info("here " + request);
        List<Token> source = annotator.parse(request.source()).tokens();
        List<Token> target = annotator.parse(request.target()).tokens();
        List<Token> input = annotator.parse(request.input()).tokens();

        List<Annotation> sourceTarget = annotator.annotate(
            source,
            target
        );
        List<Token> importantTargetTokens = sourceTarget
            .stream()
            .filter(annotation -> !annotation.error().isNone())
            .map(Annotation::targetTokens)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        List<Annotation> inputTarget = annotator.annotate(
            input,
            target
        );
        List<WordEdit> words = inputTarget
            .stream()
            .map(annotation -> {
                var correction = mapToCorrectionOperation(annotation
                    .edit()
                    .operation());
                var sourceWord = createWord(annotation.sourceTokens());
                var targetWord = createWord(annotation.targetTokens());
                var error = annotation.error().tag();
                return new WordEdit(sourceWord, targetWord, correction, error);
            })
            .collect(Collectors.toList());
        double penalty = inputTarget
            .stream()
            .mapToDouble(annotation -> {
                // get penalty
                boolean isImportant = annotation
                    .targetTokens()
                    .stream()
                    .anyMatch(importantTargetTokens::contains);
                return scoringStrategy.getPenalty(
                    annotation,
                    isImportant
                );
            })
            .sum();

        double score = 1 - penalty;

        GrammarAssessmentResponse response =
            new GrammarAssessmentResponse(
            score,
            words
        );
        logger.info(response.toString());
        return response;
    }

    private Correction mapToCorrectionOperation(Operation operation) {
        return switch (operation) {
            case EQUAL -> Correction.NONE;
            case INSERT -> Correction.INSERT;
            case DELETE -> Correction.DELETE;
            case SUBSTITUTE -> Correction.SUBSTITUTE;
            case TRANSPOSE -> Correction.TRANSPOSE;
        };
    }

    private Word createWord(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return new Word("", "");
        }
        Token last = tokens.get(tokens.size() - 1);
        String text = tokens
            .stream()
            .map(token -> token.text() + token.spaceAfter())
            .collect(Collectors.joining())
            .trim();
        return new Word(text, last.spaceAfter());
    }
}
