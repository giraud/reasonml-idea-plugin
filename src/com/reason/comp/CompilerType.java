package com.reason.comp;

import org.jetbrains.annotations.*;

public enum CompilerType {
    BS("BuckleScript"),
    RESCRIPT("Rescript"),
    DUNE("Dune"),
    ESY("Esy");

    private final String displayName;

    CompilerType(String displayName) {
        this.displayName = displayName;
    }

    public @NotNull String displayName() {
        return displayName;
    }
}
