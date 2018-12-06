package com.reason.build.dune;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.build.CompilerProcessLifecycle;
import com.reason.build.annotations.ErrorsManager;
import com.reason.build.annotations.OutputInfo;
import com.reason.ide.hints.InferredTypesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class DuneOutputListener implements ProcessListener {

    private static final Pattern FILE_LOCATION = Pattern.compile("File \\\"(.+)\\\", line (\\d+), characters (\\d+)-(\\d+):\n");

    enum BuildStatus {
        fine,
        warning,
        error
    }

    @NotNull
    private final Project m_project;
    private final ErrorsManager m_errorsManager;
    private final CompilerProcessLifecycle m_compilerLifecycle;
    @NotNull
    private final Logger m_log;
    private final List<OutputInfo> m_bsbInfo = new ArrayList<>();

    @Nullable
    private OutputInfo m_latestInfo = null;

    DuneOutputListener(Project project, CompilerProcessLifecycle compilerLifecycle) {
        m_project = project;
        m_errorsManager = project.getComponent(ErrorsManager.class);
        m_compilerLifecycle = compilerLifecycle;
        m_log = Logger.getInstance("ReasonML.build");
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        m_bsbInfo.clear();
        m_errorsManager.clearErrors();
        //m_previousText = "";
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        m_compilerLifecycle.terminated();

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

        /*
        File "CmtExtractor.ml", line 80, characters 67-70:
        Error: Unbound value cmt
        Hint: Did you mean cmtx?
        */
        if (text.startsWith("File")) {
            m_latestInfo = extractExtendedFilePositions(text);
            if (m_latestInfo != null) {
                m_latestInfo.isError = false;
            }
        } else if (text.startsWith("Error:")) {
            if (m_latestInfo != null) {
                m_latestInfo.isError = true;
                m_latestInfo.message = text.substring(6);
            }
        } else if (text.startsWith("Hint:")) {
            if (m_latestInfo != null) {
                m_latestInfo.message += " (" + text + ")";
            }
        }

        //m_previousText = text;
    }

    // File "...path/src/Source.ml", line 111, characters 0-3:
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
                if (info == null || info.colStart < 0 || info.colEnd < 0) {
                    m_log.error("Can't decode columns for [" + text + "]");
                    return null;
                }
                return info;
            }
        }

        return null;
    }

    @Nullable
    private OutputInfo addInfo(@NotNull String path, @NotNull String line, @NotNull String colStart, @NotNull String colEnd) {
        OutputInfo info = new OutputInfo();

        VirtualFile baseDir = m_project.getBaseDir();
        VirtualFile child = baseDir.findChild(path);
        if (child == null) {
            return null;
        }

        info.path = child.getCanonicalPath();
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

    private void reset() {
//        m_status = fine;
//        m_failedLine = -1;
        m_latestInfo = null;
    }
}
