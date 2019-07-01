package com.reason.build;

import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.OCamlSdk;
import com.reason.Platform;
import com.reason.build.bs.Bucklescript;
import com.reason.build.console.CliType;
import com.reason.build.dune.DuneManager;
import com.reason.ide.ORNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public class CompilerManager implements ApplicationComponent {

    private static final Compiler DUMMY_COMPILER = new Compiler() {
        @Override
        public void refresh(@NotNull VirtualFile bsconfigFile) {
            //nothing
        }

        @Override
        public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {
            //nothing
        }
    };

    public static CompilerManager getInstance() {
        return ApplicationManager.getApplication().getComponent(CompilerManager.class);
    }

    @NotNull
    public Compiler getCompiler(@NotNull Project project) {
        VirtualFile duneConfig = Platform.findBaseRoot(project).findChild("jbuild");
        if (duneConfig != null) {
            Sdk odk = OCamlSdk.getSDK(project);
            if (odk == null) {
                Notifications.Bus.notify(new ORNotification("Dune",
                        "<html>Can't find sdk.\n"
                                + "When using a dune config file, you need to create an OCaml SDK and associate it to the project.\n"
                                + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#ocaml\">github</a>.</html>",
                        ERROR, URL_OPENING_LISTENER));
                return DUMMY_COMPILER;
            }
            return ServiceManager.getService(project, DuneManager.class);
        } else {
            return ServiceManager.getService(project, Bucklescript.class);
        }
    }
}
