package com.reason;

import com.intellij.execution.process.ProcessHandler;
import com.reason.ide.console.CliType;

public interface CompilerProcess {

    boolean start();

    void startNotify();

    ProcessHandler recreate(CliType cliType, Compiler.ProcessTerminated onProcessTerminated);

    void terminate();

}
