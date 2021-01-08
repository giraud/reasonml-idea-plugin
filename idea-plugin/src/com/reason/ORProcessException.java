package com.reason;

import org.jetbrains.annotations.NotNull;

public class ORProcessException extends RuntimeException {

  public ORProcessException(@NotNull String message) {
    super(message);
  }

  public ORProcessException(String message, Throwable cause) {
    super(message, cause);
  }
}
