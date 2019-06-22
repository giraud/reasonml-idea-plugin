package com.reason.build.bs.compiler;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.AnsiEscapeDecoder;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.reason.build.Compiler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BsProcessHandler extends KillableProcessHandler implements AnsiEscapeDecoder.ColoredTextAcceptor {

    private final AnsiEscapeDecoder m_ansiEscapeDecoder = new AnsiEscapeDecoder();
    @Nullable
    private final Compiler.ProcessTerminated m_onProcessTerminated;
    @Nullable
    private RawProcessListener m_listener = null;

    BsProcessHandler(@NotNull GeneralCommandLine commandLine, @Nullable Compiler.ProcessTerminated onProcessTerminated) throws ExecutionException {
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
        String cleanedText = SystemInfo.isWindows ? text.replace("â”†", "|") : text;

        StringBuilder sb = new StringBuilder();
        m_ansiEscapeDecoder.escapeText(cleanedText, outputType, (chunk, attributes) -> {
            sb.append(chunk);
            super.notifyTextAvailable(chunk, attributes);
        });

        if (m_listener != null) {
            m_listener.onRawTextAvailable(sb.toString());
        }
    }

    void addRawProcessListener(RawProcessListener listener) {
        if (m_listener != null) {
            removeProcessListener(m_listener);
        }
        m_listener = listener;
        super.addProcessListener(listener);
    }

    @Override
    public void coloredTextAvailable(@NotNull String text, @NotNull Key attributes) {
        super.notifyTextAvailable(text, attributes);
    }
}
