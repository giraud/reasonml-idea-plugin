package com.reason.bs;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.bs.console.ConsoleBus;

import static java.lang.Integer.parseInt;

public class BsbOutputListener implements ProcessListener {
    private final ConsoleBus m_consoleNotifier;
    private final BsbErrorsManager m_errorsManager;
    private String m_fileProcessed = "";
    private boolean m_failed;
    private int m_failedLine;
    private BsbErrorsManager.BsbError m_bsbError;

    BsbOutputListener(ConsoleBus consoleNotifier, Project project) {
        m_consoleNotifier = consoleNotifier;
        m_errorsManager = BsbErrorsManager.getInstance(project);
    }

    @Override
    public void startNotified(ProcessEvent event) {
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        m_consoleNotifier.processTerminated();
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String text = event.getText();
        if (text.charAt(0) != '\n' && text.charAt(0) != ' ' && m_failedLine > 0) {
            m_bsbError.message = "Found error at L" + m_bsbError.line + " for " + m_fileProcessed;
            System.out.println(m_bsbError.message);
            m_errorsManager.setError(m_fileProcessed, m_bsbError);
            reset();
        }

        if (m_failed) {
            // The third line contains file information, I hope it won't change
            if (m_failedLine == 3) {
                // Extract file path and error position
                String[] tokens = text.trim().split(" ");
                if (tokens.length == 2) {
                    m_fileProcessed = tokens[0];
                    String[] positions = tokens[1].split(":");
                    if (positions.length == 2) {
                        String line = positions[0];
                        String[] columns = positions[1].split("-");
                        // If everything went ok, creates a new error
                        m_bsbError = new BsbErrorsManager.BsbError();
                        m_bsbError.line = parseInt(line);
                        m_bsbError.colStart = parseInt(columns[0]);
                        m_bsbError.colEnd = parseInt(columns.length == 1 ? columns[0] : columns[1]) + 1;
                    }
                }
            }
            m_failedLine++;
        }
        else if (text.startsWith(">>>> Start compiling")) {
            System.out.println("Reset all errors");
            m_errorsManager.clearErrors("");
        }
        else if (text.startsWith(">>>> Finish compiling")) {
            reset();
            // Refresh external annotator !?
        } else if (text.startsWith("Building")) {
            reset();
        }
        else if (text.startsWith("FAILED")) {
            m_failed = true;
            m_failedLine = 0;
        }
    }

    private void reset() {
        m_failed = false;
        m_failedLine = -1;
        m_fileProcessed = "";
        m_bsbError = null;
    }
}
