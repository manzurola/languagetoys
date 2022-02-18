package com.github.manzurola.languagetoys.modules.grammar;

import com.github.manzurola.errant4j.core.Annotation;
import com.github.manzurola.errant4j.core.errors.ErrorCategory;
import com.github.manzurola.errant4j.core.errors.GrammaticalError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PenaltyScoringStrategy {

    private final double maxScore = 1.0;
    private final double importantWordPenalty = 1.0;
    private final double defaultCategoryPenalty = 0.05;
    private final Map<ErrorCategory, Double> errorCategoryPenalties =
        new HashMap<>();
    private final Set<GrammaticalError> overrideErrors = new HashSet<>();

    public PenaltyScoringStrategy() {
        errorCategoryPenalties.put(ErrorCategory.ORTH, 0.02);
        errorCategoryPenalties.put(ErrorCategory.PUNCT, 0.02);
        errorCategoryPenalties.put(ErrorCategory.SPELL, 0.01);

        overrideErrors.add(GrammaticalError.REPLACEMENT_ORTHOGRAPHY);
        overrideErrors.add(GrammaticalError.MISSING_PUNCTUATION);
        overrideErrors.add(GrammaticalError.REPLACEMENT_PUNCTUATION);
        overrideErrors.add(GrammaticalError.UNNECESSARY_PUNCTUATION);
        overrideErrors.add(GrammaticalError.REPLACEMENT_SPELLING);
    }

    public double getPenalty(
        Annotation annotation,
       boolean isImportant
    ) {
        if (annotation.error().isNone()) {
            return 0.0;
        }

        boolean overrideImportant = overrideErrors.contains(annotation.error());
        if (isImportant && !overrideImportant) {
            return importantWordPenalty;
        }
        return errorCategoryPenalties.computeIfAbsent(
            annotation.error().category(),
            c -> defaultCategoryPenalty
        );
    }

}
