package com.github.manzurola.languagetoys.api.grammar;

import com.github.manzurola.errant4j.core.Annotation;
import com.github.manzurola.errant4j.core.Annotator;
import com.github.manzurola.errant4j.core.errors.GrammaticalError;
import com.github.manzurola.spacy4j.api.containers.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        Question question = request.question();
        Answer answer = request.answer();
        List<Token> source = annotator.parse(question.source()).tokens();
        List<Token> target = annotator.parse(question.target()).tokens();
        List<Token> input = annotator.parse(answer.input()).tokens();

        List<com.github.manzurola.errant4j.core.Annotation> sourceTarget =
            annotator.annotate(
                source,
                target
            );
        List<Token> importantTargetTokens = sourceTarget
            .stream()
            .filter(annotation -> !annotation.error().isNone())
            .map(com.github.manzurola.errant4j.core.Annotation::targetTokens)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        List<com.github.manzurola.errant4j.core.Annotation> inputTarget =
            annotator.annotate(
                input,
                target
            );
        List<Edit> words = inputTarget
            .stream()
            .map(annotation -> {
                List<Word> sourceWords = getWords(annotation.sourceTokens());
                List<Word> targetWords = getWords(annotation.targetTokens());
                Optional<Error> error = getError(annotation);
                String operation = getOperation(annotation);
                return new Edit(
                    sourceWords,
                    targetWords,
                    operation,
                    error
                );
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
            new GrammarAssessmentResponse(new ScoredAnswer(score, words));
        logger.info(response.toString());
        return response;
    }

    private String getOperation(Annotation annotation) {
        return annotation.edit().operation().name().toLowerCase();
    }

    private Optional<Error> getError(Annotation annotation) {
        GrammaticalError error = annotation.error();
        if (error.isNone() || error.isIgnored()) {
            return Optional.empty();
        }
        String category = error.category().tag().toLowerCase();
        String type = error.type().name().toLowerCase();
        String id = String.format("%s-%s", type, category);
        return Optional.of(new Error(id, type, category));
    }

    private List<Word> getWords(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return List.of();
        }
        return tokens
            .stream()
            .map(token -> new Word(token.text(), token.spaceAfter()))
            .collect(Collectors.toList());
    }
}
