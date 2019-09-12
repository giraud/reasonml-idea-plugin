package com.reason.bs;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.Log;
import com.reason.ide.annotations.ErrorsManager;
import com.reason.ide.annotations.OutputInfo;
import com.reason.ide.hints.InferredTypesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.reason.bs.BsOutputListener.LineProcessor.BuildStatus.*;
import static java.lang.Integer.parseInt;

public class BsOutputListener implements RawProcessListener {

    private static final Pattern FILE_LOCATION = Pattern.compile("File \"(.+)\", line (\\d+), characters (\\d+)-(\\d+):\n");
    private static final Log LOG = Log.create("build");

    private final Project m_project;
    private final BsProcess m_compiler;
    private final LineProcessor m_lineProcessor;

    BsOutputListener(@NotNull Project project, @NotNull BsProcess bsc) {
        m_project = project;
        m_compiler = bsc;
        m_lineProcessor = new LineProcessor();
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        ServiceManager.getService(m_project, ErrorsManager.class).clearErrors();
        m_lineProcessor.m_bsbInfo.clear();
//        m_lineProcessor.m_previousText = "";
    }

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
    }

    @Override
    public void onRawTextAvailable(@NotNull String text) {
        m_lineProcessor.onRawTextAvailable(text);
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        m_compiler.terminated();

        if (!m_lineProcessor.m_bsbInfo.isEmpty()) {
            ServiceManager.getService(m_project, ErrorsManager.class).addAllInfo(m_lineProcessor.m_bsbInfo);
        }

        m_lineProcessor.reset();
        m_lineProcessor.m_bsbInfo.clear();

        ApplicationManager.getApplication().invokeLater(() -> {
            if (!m_project.isDisposed()) {
                // When build is done, we need to refresh editors to be notified of latest modifications
                DaemonCodeAnalyzer.getInstance(m_project).restart();
                EditorFactory.getInstance().refreshAllEditors();
                InferredTypesService.queryForSelectedTextEditor(m_project);
            }
        });
    }


    /**
     * Line processor is a state machine.
     */
    static class LineProcessor {

        enum BuildStatus {
            unknown,
            // warning steps
            warningDetected,
            warningLinePos,
            warningSourceExtract,
            warningMessage,
            // error steps
            errorDetected,
            errorLinePos,
            errorSourceExtract,
            errorMessage,
            // syntax error
            syntaxError
        }

        private BuildStatus m_status = BuildStatus.unknown;

        @Nullable
        private OutputInfo m_latestInfo = new OutputInfo();
        private String m_previousText = "";

        final List<OutputInfo> m_bsbInfo = new ArrayList<>();

        void onRawTextAvailable(@NotNull String text) {
            String trimmedText = text.trim();

            switch (m_status) {
                /*
                 Warning
                 */
                case warningDetected:
                    // Must contain warning location (file/position)
                    // ...path\src\File.re 61:10
                    m_latestInfo = extractFilePositions(text);
                    if (m_latestInfo != null) {
                        m_latestInfo.isError = false;
                    }
                    m_status = warningLinePos;
                    break;
                case warningLinePos:
                case errorLinePos:
                    if (m_latestInfo != null && !trimmedText.isEmpty()) {
                        m_status = m_latestInfo.isError ? errorSourceExtract : warningSourceExtract;
                    }
                    break;
                case warningSourceExtract:
                case errorSourceExtract:
                    trimmedText = text.trim();
                    if (m_latestInfo != null && trimmedText.isEmpty()) {
                        m_status = m_latestInfo.isError ? errorMessage : warningMessage;
                    }
                    break;
                case warningMessage:
                case errorMessage:
                    if (trimmedText.isEmpty() || text.charAt(0) != ' ') {
                        // create bsb info
                        if (m_latestInfo != null) {
                            m_latestInfo.message = m_latestInfo.message.trim();
                        }
                        m_status = unknown;
                    } else if (m_latestInfo != null) {
                        m_latestInfo.message += text;
                    }
                    break;
                /*
                 Error
                 */
                case errorDetected:
                    // Must contain error location (file/position)
                    // ...path\src\File.re 61:10-23
                    m_latestInfo = extractFilePositions(text);
                    if (m_latestInfo != null) {
                        m_latestInfo.isError = true;
                    }
                    m_status = errorLinePos;
                    break;
                default:
                    if (trimmedText.startsWith("Warning number")) {
                        reset();
                        m_status = warningDetected;
                    } else if (text.startsWith("Error:")) {
                        // It's a one line message
                        m_status = syntaxError;
                        if (m_previousText.startsWith("File")) {
                            m_latestInfo = extractExtendedFilePositions(m_previousText);
                            if (m_latestInfo != null) {
                                m_latestInfo.message = text.substring(6).trim();
                            }
                        }
                    } else if (trimmedText.startsWith("We've found a bug for you")) {
                        if (m_status != syntaxError) {
                            reset();
                            m_status = errorDetected;
                        }
                    } else if (m_latestInfo != null && trimmedText.startsWith("Hint:")) {
                        m_latestInfo.message += ". " + text.trim();
                    }
            }

            m_previousText = text;
        }

        private void reset() {
            m_status = unknown;
            m_latestInfo = null;
            m_previousText = "";
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
                        LOG.error("Can't decode columns for [" + text + "]");
                        return null;
                    }
                    return info;
                }
            }

            return null;
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
                            LOG.error("Can't decode columns for [" + text + "]");
                            return null;
                        }
                        return info;
                    }
                }
            }

            return null;
        }

        @NotNull
        private OutputInfo addInfo(@NotNull String path, @NotNull String lineStart, @NotNull String
                colStart, @Nullable String lineEnd, @Nullable String colEnd) {
            OutputInfo info = new OutputInfo();
            info.path = path;
            info.lineStart = parseInt(lineStart);
            info.colStart = parseInt(colStart);
            info.lineEnd = lineEnd == null ? info.lineStart : parseInt(lineEnd);
            info.colEnd = colEnd == null ? info.colStart + 1 : parseInt(colEnd) + 1;
            m_bsbInfo.add(info);
            return info;
        }

        @NotNull
        private OutputInfo addInfo(@NotNull String path, @NotNull String line, @NotNull String
                colStart, @NotNull String colEnd) {
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
    }
}
