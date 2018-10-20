package com.reason.build;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.dune.DuneManager;
import com.reason.ide.sdk.OCamlSDK;
import org.jetbrains.annotations.NotNull;

public class CompilerManager implements ApplicationComponent {
    public static CompilerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(CompilerManager.class);
    }

    @NotNull
    public Compiler getCompiler(@NotNull Project project) {
        Sdk projectSDK = OCamlSDK.getSDK(project);
        return projectSDK == null ? BucklescriptManager.getInstance(project) : DuneManager.getInstance(project);
    }

}
