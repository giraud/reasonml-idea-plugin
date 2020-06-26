package com.reason.ide.console;

import com.reason.CompilerType;

public interface CliType {

    CompilerType getCompilerType();

    public static enum Bs implements CliType {
        MAKE,
        CLEAN_MAKE;

        @Override
        public CompilerType getCompilerType() {
            return CompilerType.BS;
        }
    }

    public static enum Dune implements CliType {
        BUILD,
        CLEAN,
        INSTALL;

        @Override
        public CompilerType getCompilerType() {
            return CompilerType.DUNE;
        }
    }

    public enum Esy implements CliType {
        INSTALL,
        BUILD,
        PRINT_ENV,
        SHELL;

        @Override
        public CompilerType getCompilerType() {
            return CompilerType.ESY;
        }
    }
}
