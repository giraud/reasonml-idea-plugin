package com.reason.comp.bs;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import com.reason.Log;
import com.reason.ide.annotations.OutputInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BscProcessListener implements ProcessListener {

    private static final Log LOG = Log.create("process.bsc.listener");

    final BsLineProcessor m_lineProcessor = new BsLineProcessor(LOG);
    final StringBuilder m_builder = new StringBuilder();

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        LOG.trace("start", event);
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        LOG.trace("end", event);
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();
        if (text != null) {
            // remove ansi color codes as the "-color never" flag doesn't remove all colors
            // https://stackoverflow.com/questions/14652538/remove-ascii-color-codes
            String textWithoutAnsiColors = text.replaceAll("\u001B\\[[;\\d]*m", "");
            m_builder.append(textWithoutAnsiColors);
            if (text.endsWith("\n")) {
                m_lineProcessor.onRawTextAvailable(m_builder.toString());
                if (LOG.isTraceEnabled()) {
                    LOG.trace(m_builder.toString().replace("\n", ""));
                }
                m_builder.setLength(0);
            }
        }
    }

    public @NotNull List<OutputInfo> getInfo() {
        return m_lineProcessor.getInfo();
    }
}
