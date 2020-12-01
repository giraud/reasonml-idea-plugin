package com.reason;

import org.jetbrains.annotations.NotNull;

public class CompilerORProcessException extends ORProcessException {

  public CompilerORProcessException(@NotNull String message, @NotNull CompilerType compilerType) {
    super(message(compilerType, message));
  }

  public CompilerORProcessException(
      @NotNull String message, @NotNull CompilerType compilerType, Throwable cause) {
    super(message(compilerType, message), cause);
  }

  private static @NotNull String message(@NotNull CompilerType compilerType, String message) {
    return compilerType.displayName() + " process exception. " + message;
  }
}
