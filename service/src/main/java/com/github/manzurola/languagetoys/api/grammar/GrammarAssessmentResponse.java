package com.github.manzurola.languagetoys.api.grammar;

import java.util.List;

public record GrammarAssessmentResponse(double score, List<Edit> words) {
}
