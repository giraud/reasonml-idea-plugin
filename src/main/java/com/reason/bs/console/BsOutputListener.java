package com.reason.bs.console;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.bs.annotations.BsErrorsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.reason.bs.console.BsOutputListener.BuildStatus.*;
import static java.lang.Integer.parseInt;

public class BsOutputListener implements ProcessListener {

    enum BuildStatus {
        fine,
        warning,
        error
    }

    private final Bucklescript m_bucklescript;
    private final Project m_project;
    private final List<BsErrorsManager.BsbInfo> m_bsbInfo = new ArrayList<>();

    private BuildStatus m_status;
    private int m_failedLine;
    private BsErrorsManager.BsbInfo m_latestInfo = null;

    BsOutputListener(Project project) {
        m_project = project;
        m_bucklescript = BucklescriptProjectComponent.getInstance(project);
    }

    @Override
    public void startNotified(ProcessEvent event) {
        m_bsbInfo.clear();
        m_bucklescript.clearErrors();
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        if (!m_bsbInfo.isEmpty()) {
            m_bucklescript.addAllInfo(m_bsbInfo);
        }

        reset();
        m_bsbInfo.clear();

        DaemonCodeAnalyzer codeAnalyzer = DaemonCodeAnalyzer.getInstance(m_project);
        codeAnalyzer.restart();

        // When build is done, we need to refresh VFS to be notified of latest modifications
        ApplicationManager.getApplication().invokeLater(() -> VirtualFileManager.getInstance().syncRefresh());
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String text = event.getText();

        if (m_status == error) {
            if (m_failedLine == 1) {
                // Extract file path and error position
                m_latestInfo = extractFilePositions(text);
            }

            if (text.startsWith("Error:") && m_latestInfo != null) {
                m_latestInfo.message = text;
            }

            if (text.charAt(0) != '\n' && text.charAt(0) != ' ' && m_failedLine > 0) {
                reset();
                return;
            }

            // The third line contains file information, I hope it won't change
            //if (m_failedLine == 3) {
            // Extract file path and error position
            //m_latestInfo = extractFilePositions(text);
            //}

            if (2 < m_failedLine && 2 < text.length()) {
                char c = text.charAt(2);
                if ('\n' != c && (c < '0' || '9' < c) && m_latestInfo != null) {
                    m_latestInfo.message += text;
                }
            }

            m_failedLine++;
        } else if (m_status == warning) {
            if (m_failedLine == 1) {
                // extract file and position
                // ...path\src\File.re 61:10
                m_latestInfo = extractFilePositions(text);
                if (m_latestInfo != null) {
                    m_latestInfo.isError = false;
                }
            }

            // Warning message ends with a blank new line
            if (text.charAt(0) == '\n' && 4 < m_failedLine) {
                reset();
                return;
            }

            if (4 < m_failedLine && 2 < text.length()) {
                char c = text.charAt(2);
                if ('\n' != c && (c < '0' || '9' < c) && m_latestInfo != null) {
                    m_latestInfo.message += text;
                }
            }

            m_failedLine++;
        } else if (text.contains("Warning")) {
            m_status = warning;
            m_failedLine = 1;
        } else if (text.contains("We've found a bug")) {
            m_status = error;
            m_failedLine = 1;
        }
    }

    @Nullable
    private BsErrorsManager.BsbInfo extractFilePositions(@Nullable String text) {
        if (text != null) {
            String[] tokens = text.trim().split(" ");
            if (tokens.length == 2) {
                String path = tokens[0];
                String[] positions = tokens[1].split(":");
                if (positions.length == 2) {
                    String line = positions[0];
                    String[] columns = positions[1].split("-");
                    // If everything went ok, creates a new error
                    return addInfo(path, line, columns);
                }
            }
        }

        return null;
    }

    private BsErrorsManager.BsbInfo addInfo(@NotNull String path, @NotNull String line, @NotNull String[] columns) {
        BsErrorsManager.BsbInfo info = new BsErrorsManager.BsbInfo();
        info.path = path;
        info.line = parseInt(line);
        info.colStart = parseInt(columns[0]);
        info.colEnd = parseInt(columns.length == 1 ? columns[0] : columns[1]);
        m_bsbInfo.add(info);
        return info;
    }

    private void reset() {
        m_status = fine;
        m_failedLine = -1;
        m_latestInfo = null;
    }
}
