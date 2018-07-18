package com.reason.build;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public interface Compiler {

    void refresh(@NotNull VirtualFile bsconfigFile);

    void run(@NotNull VirtualFile file);

}
