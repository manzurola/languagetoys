package com.github.manzurola.languagetoys.modules.grammar;

public record WordEdit(Word source,
                       Word target,
                       Correction correction,
                       String error) {
}
