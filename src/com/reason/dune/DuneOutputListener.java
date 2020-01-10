package com.reason.dune;

import java.util.*;
import java.util.regex.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.CompilerProcessLifecycle;
import com.reason.Platform;
import com.reason.ide.annotations.ErrorsManager;
import com.reason.ide.annotations.OutputInfo;
import com.reason.ide.hints.InferredTypesService;

import static java.lang.Integer.parseInt;

public class DuneOutputListener implements ProcessListener {

    private static final Pattern FILE_LOCATION = Pattern.compile("File \"(.+)\", line (\\d+), characters (\\d+)-(\\d+):\n");

    enum BuildStatus {
        fine, warning, error
    }

    @NotNull
    private final Project m_project;
    private final CompilerProcessLifecycle m_compilerLifecycle;
    @NotNull
    private final Logger m_log;
    private final List<OutputInfo> m_bsbInfo = new ArrayList<>();

    @Nullable
    private OutputInfo m_latestInfo = null;

    DuneOutputListener(@NotNull Project project, CompilerProcessLifecycle compilerLifecycle) {
        m_project = project;
        m_compilerLifecycle = compilerLifecycle;
        m_log = Logger.getInstance("ReasonML.build");
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        m_bsbInfo.clear();
        ServiceManager.getService(m_project, ErrorsManager.class).clearErrors();
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        m_compilerLifecycle.terminated();

        if (!m_bsbInfo.isEmpty()) {
            ServiceManager.getService(m_project, ErrorsManager.class).addAllInfo(m_bsbInfo);
        }

        reset();
        m_bsbInfo.clear();

        ApplicationManager.getApplication().invokeLater(() -> {
            // When a build is done, we need to refresh editors to be notified of the latest modifications
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
                    m_log.error("Can't decode columns for [" + text.replace("\n", "") + "]");
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

        VirtualFile fileInError = Platform.findFileByRelativePath(m_project, path);
        if (fileInError == null) {
            return null;
        }

        info.path = fileInError.getCanonicalPath();
        info.lineStart = parseInt(line);
        info.colStart = parseInt(colStart) + 1;
        info.lineEnd = info.lineStart;
        info.colEnd = parseInt(colEnd) + 1;
        if (info.colEnd == info.colStart) {
            info.colEnd += 1;
        }

        m_bsbInfo.add(info);
        return info;
    }

    private void reset() {
        m_latestInfo = null;
    }
}
