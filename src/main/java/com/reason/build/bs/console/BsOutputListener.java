package com.reason.build.bs.console;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.build.annotations.ErrorsManager;
import com.reason.build.annotations.OutputInfo;
import com.reason.build.bs.compiler.BsCompiler;
import com.reason.ide.hints.InferredTypesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.reason.build.bs.console.BsOutputListener.BuildStatus.*;
import static java.lang.Integer.parseInt;

public class BsOutputListener implements ProcessListener {

    private static final Pattern FILE_LOCATION = Pattern.compile("File \\\"(.+)\\\", line (\\d+), characters (\\d+)-(\\d+):\n");

    enum BuildStatus {
        fine,
        warning,
        error
    }

    private final Project m_project;
    private final ErrorsManager m_errorsManager;
    private final BsCompiler m_compiler;
    private final Logger m_log;
    private final List<OutputInfo> m_bsbInfo = new ArrayList<>();

    private BuildStatus m_status;
    private int m_failedLine;
    private OutputInfo m_latestInfo = null;
    private String m_previousText;

    BsOutputListener(Project project, BsCompiler bsc) {
        m_project = project;
        m_errorsManager = project.getComponent(ErrorsManager.class);
        m_compiler = bsc;
        m_log = Logger.getInstance("ReasonML.bsb");
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        m_bsbInfo.clear();
        m_errorsManager.clearErrors();
        m_previousText = "";
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (m_compiler != null) {
            m_compiler.terminated();
        }

        if (!m_bsbInfo.isEmpty()) {
            m_errorsManager.addAllInfo(m_bsbInfo);
        }

        reset();
        m_bsbInfo.clear();

        ApplicationManager.getApplication().invokeLater(() -> {
            // When build is done, we need to refresh editors to be notified of latest modifications
            DaemonCodeAnalyzer.getInstance(m_project).restart();
            EditorFactory.getInstance().refreshAllEditors();
            InferredTypesService.queryForSelectedTextEditor(m_project);
        });
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();

        if (m_status == error) {
            if (m_failedLine == 1) {
                // Extract file path and error position
                m_latestInfo = extractFilePositions(text);
            }

            if (text.charAt(0) != '\n' && text.charAt(0) != ' ' && m_failedLine > 0) {
                reset();
            } else {
                if (2 < m_failedLine && 2 < text.length()) {
                    char c = text.charAt(2);
                    if ('\n' != c && (c < '0' || '9' < c) && m_latestInfo != null) {
                        m_latestInfo.message += text;
                    }
                }

                m_failedLine++;
            }
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
            } else {
                if (4 < m_failedLine && 2 < text.length()) {
                    char c = text.charAt(2);
                    if (m_latestInfo != null) {
                        if ('\n' != c && (c < '0' || '9' < c)) {
                            m_latestInfo.message += text;
                        } else if (!m_latestInfo.message.isEmpty()) {
                            m_latestInfo.message = "";
                        }
                    }
                }

                m_failedLine++;
            }
        } else if (text.startsWith("Warning")) {
            // Bsb output is not normalized and there are many form of warning/error !
            if (m_previousText.startsWith("File")) {
                // Special warning info
                m_latestInfo = extractExtendedFilePositions(m_previousText);
                if (m_latestInfo != null) {
                    m_latestInfo.isError = false;
                    int pos = text.indexOf(":");
                    m_latestInfo.message = 0 <= pos ? text.substring(pos + 1).trim() : text.substring(6);
                }
            }
        } else if (text.contains("Warning")) {
            m_status = warning;
            m_failedLine = 1;
        } else if (text.contains("We've found a bug")) {
            m_status = error;
            m_failedLine = 1;
        } else if (text.startsWith("Error:")) {
            // It's a one line message
            if (m_previousText.startsWith("File")) {
                m_latestInfo = extractExtendedFilePositions(m_previousText);
                if (m_latestInfo != null) {
                    String skippedText = text.substring(6);
                    int pos = text.indexOf(":");
                    m_latestInfo.message = 0 <= pos ? skippedText.substring(pos) : skippedText;
                }
            }
        }

        m_previousText = text;
    }

    // ...path/src/Source.re 111:21-112:22
    // ...path/src/Source.re 111:21-22
    // ...path/src/Source.re 111:21   <- must add 1 to colEnd
    @Nullable
    private OutputInfo extractFilePositions(@Nullable String text) {
        if (text != null) {
            String[] tokens = text.trim().split(" ");
            if (tokens.length == 2) {
                String path = tokens[0];
                String[] positions = tokens[1].split("-");
                if (positions.length == 1) {
                    String[] start = positions[0].split(":");
                    if (2 == start.length) {
                        return addInfo(path, start[0], start[1], null, null);
                    }
                } else if (positions.length == 2) {
                    String[] start = positions[0].split(":");
                    String[] end = positions[1].split(":");
                    OutputInfo info = addInfo(path, start[0], start[1], end.length == 1 ? start[0] : end[0], end[end.length - 1]);
                    if (info.colStart < 0 || info.colEnd < 0) {
                        m_log.error("Can't decode columns for [" + text + "]");
                        return null;
                    }
                    return info;
                }
            }
        }

        return null;
    }

    // File "...path/src/Source.re", line 111, characters 0-3:
    @Nullable
    private OutputInfo extractExtendedFilePositions(@Nullable String text) {
        if (text != null) {
            Matcher matcher = FILE_LOCATION.matcher(text);
            if (matcher.matches()) {
                String path = matcher.group(1);
                String line = matcher.group(2);
                String colStart = matcher.group(3);
                String colEnd = matcher.group(4);
                OutputInfo info = addInfo(path, line, colStart, colEnd);
                if (info.colStart < 0 || info.colEnd < 0) {
                    m_log.error("Can't decode columns for [" + text + "]");
                    return null;
                }
                return info;
            }
        }

        return null;
    }

    private OutputInfo addInfo(@NotNull String path, @NotNull String line, @NotNull String colStart, String colEnd) {
        OutputInfo info = new OutputInfo();
        info.path = path;
        info.lineStart = parseInt(line);
        info.colStart = parseInt(colStart);
        info.lineEnd = info.lineStart;
        info.colEnd = parseInt(colEnd);
        if (info.colEnd == info.colStart) {
            info.colEnd += 1;
        }
        m_bsbInfo.add(info);
        return info;
    }

    private OutputInfo addInfo(@NotNull String path, @NotNull String lineStart, @NotNull String colStart, @Nullable String lineEnd, @Nullable String colEnd) {
        OutputInfo info = new OutputInfo();
        info.path = path;
        info.lineStart = parseInt(lineStart);
        info.colStart = parseInt(colStart);
        info.lineEnd = lineEnd == null ? info.lineStart : parseInt(lineEnd);
        info.colEnd = colEnd == null ? info.colStart + 1 : parseInt(colEnd);
        m_bsbInfo.add(info);
        return info;
    }

    private void reset() {
        m_status = fine;
        m_failedLine = -1;
        m_latestInfo = null;
    }
}
