package com.reason;

import com.intellij.execution.process.ProcessHandler;
import com.reason.ide.console.CliType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CompilerProcess {

  boolean start();

  void startNotify();

  @Nullable
  ProcessHandler recreate(
      @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated);

  void terminate();
}
