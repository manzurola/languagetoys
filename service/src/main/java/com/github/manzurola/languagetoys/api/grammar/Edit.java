package com.github.manzurola.languagetoys.api.grammar;

import java.util.List;
import java.util.Optional;

public record Edit(List<Word> source,
                   List<Word> target,
                   String operation,
                   Optional<Error> error) {
}
