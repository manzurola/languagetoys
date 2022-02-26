package com.github.manzurola.languagetoys.api.grammar;

import java.util.List;

public record ScoredAnswer(double score, List<Edit> edits) {
}
