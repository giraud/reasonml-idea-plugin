package com.reason;

import org.jetbrains.annotations.NotNull;

public enum CompilerType {
    BS("BuckleScript"),
    DUMMY("Dummy"),
    DUNE("Dune"),
    ESY("Esy");

    private final String displayName;

    CompilerType(String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public String displayName() {
        return displayName;
    }
}
