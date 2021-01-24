package com.reason.bs;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.util.*;
import com.reason.Compiler;
import org.jetbrains.annotations.*;

class BsColoredProcessHandler extends KillableProcessHandler implements AnsiEscapeDecoder.ColoredTextAcceptor {

    private final @NotNull AnsiEscapeDecoder m_ansiEscapeDecoder = new AnsiEscapeDecoder();
    private final @Nullable Compiler.ProcessTerminated m_onProcessTerminated;

    BsColoredProcessHandler(@NotNull GeneralCommandLine commandLine, @Nullable Compiler.ProcessTerminated onProcessTerminated) throws ExecutionException {
        super(commandLine);
        m_onProcessTerminated = onProcessTerminated;
    }

    @Override
    protected void onOSProcessTerminated(int exitCode) {
        super.onOSProcessTerminated(exitCode);
        if (m_onProcessTerminated != null) {
            m_onProcessTerminated.run();
        }
    }

    @Override
    public final void notifyTextAvailable(@NotNull final String text, @NotNull final Key outputType) {
        m_ansiEscapeDecoder.escapeText(
                text,
                outputType,
                (chunk, attributes) -> {
                    if (":".equals(chunk)) {
                        // Suppose this is the separator in output: C:\xxx\xxx\File.re : 1:13-14
                        super.notifyTextAvailable(" ", attributes);
                    } else {
                        super.notifyTextAvailable(chunk, attributes);
                    }
                });
    }

    @Override
    public void coloredTextAvailable(@NotNull String text, @NotNull Key attributes) {
        super.notifyTextAvailable(text, attributes);
    }
}
