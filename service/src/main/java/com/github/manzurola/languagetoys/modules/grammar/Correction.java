package com.github.manzurola.languagetoys.modules.grammar;

public enum Correction {

    NONE("NONE"),
    INSERT("INSERT"),
    DELETE("DELETE"),
    SUBSTITUTE("SUBSTITUTE"),
    TRANSPOSE("TRANSPOSE");

    private final String name;

    Correction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
