package com.reason.ide.debug;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class OCamlApplicationRunningState extends CommandLineState {
    private final Module m_module;

    protected OCamlApplicationRunningState(ExecutionEnvironment environment, Module module) {
        super(environment);
        m_module = module;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = getCommand();
        return new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
    }

    private GeneralCommandLine getCommand() {
        GeneralCommandLine commandLine = new GeneralCommandLine();
        // set exe/working dir/...
        TextConsoleBuilder consoleBuilder = TextConsoleBuilderFactory.getInstance().createBuilder(m_module.getProject());
        setConsoleBuilder(consoleBuilder);
        return commandLine;
    }

}
