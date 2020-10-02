package com.reason;

import org.jetbrains.annotations.NotNull;

public enum CompilerType {
  BS("BuckleScript"),
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
