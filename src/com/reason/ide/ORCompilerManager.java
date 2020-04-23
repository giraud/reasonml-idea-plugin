package com.reason.ide;

import com.intellij.facet.FacetManager;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.Log;
import com.reason.CompilerType;
import com.reason.ORNotification;
import com.reason.bs.Bucklescript;
import com.reason.dune.DuneCompiler;
import com.reason.ide.console.CliType;
import com.reason.ide.facet.DuneFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public class ORCompilerManager {

    private static final Log LOG = Log.create("manager.compiler");

    private static final Compiler DUMMY_COMPILER = new Compiler() {
        @Nullable
        @Override
        public VirtualFile findContentRoot(@NotNull Project project) {
            return null;
        }

        @Override
        public void refresh(@NotNull VirtualFile bsconfigFile) {}

        @Override
        public void run(@NotNull VirtualFile file, @NotNull CliType cliType, @Nullable Compiler.ProcessTerminated onProcessTerminated) {}

        @Override
        public CompilerType getType() {
            return CompilerType.DUMMY;
        }
    };

    public static ORCompilerManager getInstance() {
        return ServiceManager.getService(ORCompilerManager.class);
    }

    @NotNull
    public Compiler getCompiler(@NotNull Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            DuneFacet duneFacet = FacetManager.getInstance(module).getFacetByType(DuneFacet.ID);
            if (duneFacet != null) {
                Sdk odk = duneFacet.getODK();
                if (odk == null) {
                    Notifications.Bus.notify(new ORNotification("Dune",
                                                                "<html>Can't find sdk.\n"
                                    + "When using a dune config file, you need to create an OCaml SDK and associate it to the project.\n"
                                    + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#ocaml\">github</a>.</html>",
                                                                ERROR, URL_OPENING_LISTENER));
                    return DUMMY_COMPILER;
                }
                return DuneCompiler.getInstance(project);
            }
        }

        return ServiceManager.getService(project, Bucklescript.class);
    }
}
