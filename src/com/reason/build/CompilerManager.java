package com.reason.build;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.console.CliType;
import com.reason.build.dune.DuneManager;
import com.reason.ide.sdk.OCamlSDK;

public class CompilerManager implements ApplicationComponent {

    private static final Compiler DUMMY_COMPILER = new Compiler() {
        @Override
        public void refresh(@NotNull VirtualFile bsconfigFile) {
            //nothing
        }

        @Override
        public void run(@NotNull VirtualFile file) {
            //nothing
        }

        @Override
        public void run(@NotNull VirtualFile file, @NotNull CliType cliType) {
            //nothing
        }
    };

    public static CompilerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(CompilerManager.class);
    }

    @NotNull
    public Compiler getCompiler(@NotNull Project project) {
        Sdk projectSDK = OCamlSDK.getSDK(project);
        if (projectSDK == null) {
            return BucklescriptManager.getInstance(project);
        } else {
            VirtualFile duneConfig = Platform.findBaseRoot(project).findChild("jbuild");
            if (duneConfig == null) {
                return DUMMY_COMPILER;
            } else {
                return DuneManager.getInstance(project);
            }
        }
    }
}
