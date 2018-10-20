package com.reason.build;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.build.console.CliType;
import org.jetbrains.annotations.NotNull;

public interface Compiler {

    void refresh(@NotNull VirtualFile bsconfigFile);

    void run(@NotNull VirtualFile file);

    void run(@NotNull VirtualFile file, @NotNull CliType cliType);

}
