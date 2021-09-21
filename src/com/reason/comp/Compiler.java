package com.reason.comp;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

public interface Compiler {
    enum CompilerType {
        BS("BuckleScript"),
        RESCRIPT("Rescript"),
        DUNE("Dune"),
        ESY("Esy");

        private final String myDisplayName;

        CompilerType(String displayName) {
            myDisplayName = displayName;
        }

        public @NotNull String displayName() {
            return myDisplayName;
        }
    }

    @FunctionalInterface
    interface ProcessTerminated {
        void run();
    }

    @NotNull CompilerType getType();

    boolean isConfigured(@NotNull Project project);

    boolean isAvailable(@NotNull Project project);

    @NotNull String getFullVersion(@Nullable VirtualFile file);

    void refresh(@NotNull VirtualFile configFile);

    void runDefault(@NotNull VirtualFile file, @Nullable ProcessTerminated onProcessTerminated);

    void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ProcessTerminated onProcessTerminated);
}
