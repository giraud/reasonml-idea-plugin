package com.reason.comp.rescript;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.util.*;
import org.jetbrains.annotations.*;

public class ResProcessHandler extends KillableProcessHandler implements AnsiEscapeDecoder.ColoredTextAcceptor {
    private final @NotNull AnsiEscapeDecoder myAnsiEscapeDecoder = new AnsiEscapeDecoder();

    public ResProcessHandler(@NotNull GeneralCommandLine commandLine) throws ExecutionException {
        super(commandLine);
    }

    @Override
    public final void notifyTextAvailable(@NotNull final String text, @NotNull final Key outputType) {
        myAnsiEscapeDecoder.escapeText(text, outputType, super::notifyTextAvailable);
    }

    @Override
    public void coloredTextAvailable(@NotNull String text, @NotNull Key attributes) {
        super.notifyTextAvailable(text, attributes);
    }
}
