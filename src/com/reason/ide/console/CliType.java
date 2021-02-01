package com.reason.ide.console;

import com.reason.CompilerType;
import org.jetbrains.annotations.NotNull;

public interface CliType {

  @NotNull
  CompilerType getCompilerType();

  enum Bs implements CliType {
    MAKE,
    CLEAN_MAKE;

    @Override
    public @NotNull CompilerType getCompilerType() {
      return CompilerType.BS;
    }
  }

  enum Dune implements CliType {
    BUILD,
    CLEAN,
    INSTALL;

    @Override
    public @NotNull CompilerType getCompilerType() {
      return CompilerType.DUNE;
    }
  }

  enum Esy implements CliType {
    INSTALL,
    BUILD,
    SHELL;

    @Override
    public @NotNull CompilerType getCompilerType() {
      return CompilerType.ESY;
    }
  }
}
