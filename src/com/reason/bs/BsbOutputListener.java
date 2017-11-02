package com.reason.bs;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

import static java.lang.Integer.parseInt;

public class BsbOutputListener implements ProcessListener {
    private final BsbErrorsManager m_errorsManager;
    private final Project m_project;
    private String m_fileProcessed = "";
    private boolean m_failed;
    private int m_failedLine;
    private BsbErrorsManager.BsbError m_bsbError;

    BsbOutputListener(Project project) {
        m_project = project;
        m_errorsManager = BsbErrorsManager.getInstance(project);
    }

    @Override
    public void startNotified(ProcessEvent event) {
        m_errorsManager.clearErrors();
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        if (m_bsbError != null) {
            m_errorsManager.setError(m_fileProcessed, m_bsbError);
            reset();
        }
        DaemonCodeAnalyzer codeAnalyzer = DaemonCodeAnalyzer.getInstance(m_project);
        codeAnalyzer.restart();
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String text = event.getText();

        if (m_failed) {
            if (text.startsWith("File")) {
                // Extract file path and error position
                String[] tokens = text.trim().split(", ");
                if (tokens.length == 3) {
                    m_fileProcessed = tokens[0].substring(6, tokens[0].length() - 1);
                    String line = tokens[1].substring(5);
                    String position = tokens[2].substring(11, tokens[2].length() - 1);
                    String[] columns = position.split("-");
                    // If everything went ok, creates a new error
                    m_bsbError = new BsbErrorsManager.BsbError();
                    m_bsbError.line = parseInt(line);
                    m_bsbError.colStart = parseInt(columns[0]);
                    m_bsbError.colEnd = parseInt(columns.length == 1 ? columns[0] : columns[1]) + 1;
                }
                return;
            }
            if (text.startsWith("Error:")) {
                m_bsbError.message = text;
            }

            if (text.charAt(0) != '\n' && text.charAt(0) != ' ' && m_failedLine > 0) {
                if (m_bsbError != null) {
                    m_errorsManager.setError(m_fileProcessed, m_bsbError);
                }
                reset();
                return;
            }

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

            if (4 < m_failedLine) {
                char c = text.charAt(2);
                if ('\n' != c && (c < '0' || '9' < c)) {
                    m_bsbError.message += text;
                }
            }

            m_failedLine++;
        } else if (text.startsWith("FAILED")) {
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
