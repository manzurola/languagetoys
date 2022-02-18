package com.github.manzurola.languagetoys.modules.grammar;

import java.util.List;

public record GrammarAssessmentResponse(double score, List<WordEdit> words) {
}
