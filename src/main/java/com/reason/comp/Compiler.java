package com.reason.comp;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import jpsplugin.com.reason.*;
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

    @NotNull CompilerType getType();

    boolean isConfigured(@NotNull Project project);

    boolean isAvailable(@NotNull Project project);

    @NotNull String getFullVersion(@Nullable VirtualFile file);

    void runDefault(@NotNull VirtualFile file, @Nullable ORProcessTerminated<Void> onProcessTerminated);

    void run(@Nullable VirtualFile file, @NotNull CliType cliType, @Nullable ORProcessTerminated<Void> onProcessTerminated);

    boolean isRunning();
}
