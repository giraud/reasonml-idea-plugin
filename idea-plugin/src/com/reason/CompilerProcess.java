package com.reason;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.console.CliType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CompilerProcess {

    boolean start();

    void startNotify();

    @Nullable ProcessHandler create(@Nullable VirtualFile source, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated);

    void terminate();
}
