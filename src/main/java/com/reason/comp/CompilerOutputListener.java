package com.reason.comp;

import com.intellij.execution.process.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.ide.annotations.*;
import com.reason.ide.hints.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CompilerOutputListener implements ProcessListener {
    private static final Log LOG = Log.create("output");

    private final Project myProject;
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
                                LOG.debug("Compilation done: read new types");
                                PsiFile selectedFile = InferredTypesService.getPsiFile(myProject);
                                if (selectedFile != null) {
                                    selectedFile.putUserData(SignatureProvider.SIGNATURES_CONTEXT, null); // reset
                                    InferredTypesService.queryTypes(myProject, selectedFile);
                                }
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
