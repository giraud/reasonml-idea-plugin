package com.reason.comp;

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

public class CompilerOutputListener implements ProcessListener {
    private static final Log LOG = Log.create("output");

    private final @NotNull Project myProject;
    private final CompilerOutputAnalyzer myOutputAnalyzer;

    public CompilerOutputListener(@NotNull Project project, @NotNull CompilerOutputAnalyzer outputAnalyzer) {
        myProject = project;
        myOutputAnalyzer = outputAnalyzer;
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        myProject.getService(ErrorsManager.class).clearErrors();
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        List<OutputInfo> outputInfo = myOutputAnalyzer.getOutputInfo();
        if (!outputInfo.isEmpty() && !myProject.isDisposed()) {
            LOG.debug("Update errors manager with output results");
            myProject.getService(ErrorsManager.class).addAllInfo(outputInfo);
        }

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
        myOutputAnalyzer.onTextAvailable(text);
    }
}
