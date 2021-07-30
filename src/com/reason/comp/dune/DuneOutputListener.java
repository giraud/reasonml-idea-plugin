package com.reason.comp.dune;

import com.intellij.codeInsight.daemon.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.reason.ide.annotations.*;
import com.reason.ide.hints.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

import static java.lang.Integer.*;

public class DuneOutputListener implements ProcessListener {
    private static final Log LOG = Log.create("dune.output");
    private static final Pattern FILE_LOCATION = Pattern.compile("File \"(.+)\", line (\\d+), characters (\\d+)-(\\d+):\n");

    private final @NotNull Project myProject;
    private final List<OutputInfo> myOutputInfo = new ArrayList<>();
    private @Nullable OutputInfo myLatestInfo = null;

    public DuneOutputListener(@NotNull Project project) {
        myProject = project;
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        myOutputInfo.clear();
        myProject.getService(ErrorsManager.class).clearErrors();
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (!myOutputInfo.isEmpty() && !myProject.isDisposed()) {
            LOG.debug("Update errors manager with output results");
            myProject.getService(ErrorsManager.class).addAllInfo(myOutputInfo);
        }

        reset();
        myOutputInfo.clear();

        ApplicationManager.getApplication()
                .invokeLater(
                        () -> {
                            // When build is done, we need to refresh editors to be notified of the latest
                            // modifications
                            if (!myProject.isDisposed()) {
                                LOG.debug("Refresh editors / inferred types");
                                InferredTypesService.queryForSelectedTextEditor(myProject);
                                DaemonCodeAnalyzer.getInstance(myProject).restart();
                                EditorFactory.getInstance().refreshAllEditors();
                            }
                        },
                        ModalityState.NON_MODAL);
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
            myLatestInfo = extractExtendedFilePositions(text);
            if (myLatestInfo != null) {
                myLatestInfo.isError = false;
            }
        } else if (text.startsWith("Error:")) {
            if (myLatestInfo != null) {
                myLatestInfo.isError = true;
                myLatestInfo.message = text.substring(6);
            }
        } else if (text.startsWith("Hint:")) {
            if (myLatestInfo != null) {
                myLatestInfo.message += " (" + text + ")";
            }
        }

        // m_previousText = text;
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
                if (info.colStart < 0 || info.colEnd < 0) {
                    LOG.error("Can't decode columns for [" + text.replace("\n", "") + "]");
                    return null;
                }
                return info;
            }
        }

        return null;
    }

    @NotNull
    private OutputInfo addInfo(@NotNull String path, @NotNull String line, @NotNull String colStart, @NotNull String colEnd) {
        OutputInfo info = new OutputInfo();

        info.path = path;
        info.lineStart = parseInt(line);
        info.colStart = parseInt(colStart) + 1;
        info.lineEnd = info.lineStart;
        info.colEnd = parseInt(colEnd) + 1;
        if (info.colEnd == info.colStart) {
            info.colEnd += 1;
        }

        myOutputInfo.add(info);
        return info;
    }

    private void reset() {
        myLatestInfo = null;
    }
}
