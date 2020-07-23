package com.reason.ide.console;

import com.reason.CompilerType;

public interface CliType {

    CompilerType getCompilerType();

    enum Bs implements CliType {
        MAKE,
        CLEAN_MAKE;

        @Override
        public CompilerType getCompilerType() {
            return CompilerType.BS;
        }
    }

    enum Dune implements CliType {
        BUILD,
        CLEAN,
        INSTALL;

        @Override
        public CompilerType getCompilerType() {
            return CompilerType.DUNE;
        }
    }

    enum Esy implements CliType {
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
