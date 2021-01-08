package com.reason;

import com.intellij.execution.process.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;

public interface CompilerProcess {
    boolean start();

    void startNotify();

    @Nullable ProcessHandler create(@NotNull VirtualFile source, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated);

    void terminate();
}
