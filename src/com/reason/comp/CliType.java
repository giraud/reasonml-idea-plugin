package com.reason.comp;

import org.jetbrains.annotations.*;

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

    enum Rescript implements CliType {
        MAKE,
        CLEAN;

        @Override
        public @NotNull CompilerType getCompilerType() {
            return CompilerType.RESCRIPT;
        }
    }

    enum Dune implements CliType {
        BUILD,
        CLEAN,
        INSTALL,
        VERSION;

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
