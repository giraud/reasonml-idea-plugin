package com.reason.esy;

import com.intellij.execution.ExecutionException;

import static com.reason.esy.EsyProcessException.Type.*;

class EsyProcessException extends RuntimeException {

  static enum Type {
    ESY_EXECUTION_ERROR("Esy command failed."),
    ESY_NOT_FOUND_IN_PATH("Esy executable not found in PATH.");

    private final String message;

    private Type(String message) {
      this.message = message;
    }
  }

  public static EsyProcessException esyExecutionError() {
    return new EsyProcessException(ESY_EXECUTION_ERROR);
  }

  public static EsyProcessException esyExecutionError(ExecutionException e) {
    return new EsyProcessException(ESY_EXECUTION_ERROR, e);
  }

  public static EsyProcessException esyNotFoundException() {
    return new EsyProcessException(ESY_NOT_FOUND_IN_PATH);
  }

  private EsyProcessException(Type type) {
    super(type.message);
  }

  private EsyProcessException(Type type, Throwable cause) {
    super(type.message, cause);
  }
}
