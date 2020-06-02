package com.reason;

import org.jetbrains.annotations.NotNull;

public class CompilerProcessException extends RuntimeException {

    private CompilerProcessException() {}

    public CompilerProcessException(@NotNull String message, @NotNull CompilerType compilerType) {
        super(message(compilerType, message));
    }

    public CompilerProcessException(@NotNull String message, @NotNull CompilerType compilerType, Throwable cause) {
        super(message(compilerType, message), cause);
    }

    private static String message(CompilerType compilerType, String message) {
       return compilerType.displayName() + " process exception. " + message;
    }
}
