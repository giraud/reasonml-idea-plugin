package com.reason.ide.console;

import com.intellij.execution.ui.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.intellij.util.concurrency.*;
import com.reason.comp.Compiler;
import com.reason.comp.Compiler.*;
import com.reason.comp.*;
import com.reason.ide.*;
import com.reason.ide.console.bs.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.console.esy.*;
import com.reason.ide.console.rescript.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ORToolWindowManager {
    private final @NotNull Project myProject;

    private ORToolWindowManager(@NotNull Project project) {
        myProject = project;
    }

    public @Nullable ConsoleView getConsoleView(@NotNull String id) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(id);
        Content windowContent = toolWindow == null ? null : toolWindow.getContentManager().getContent(0);
        JComponent component = windowContent == null ? null : windowContent.getComponent();
        JComponent panel = (component instanceof SimpleToolWindowPanel) ? ((SimpleToolWindowPanel) component).getComponent() : null;

        return panel == null ? null : (ConsoleView) panel.getComponent(0);
    }

    public void showShowToolWindows() {
        setToolWindowAvailable(RescriptToolWindowFactory.ID, shouldShowRescriptToolWindow(myProject));
        setToolWindowAvailable(BsToolWindowFactory.ID, shouldShowBsToolWindow(myProject));
        setToolWindowAvailable(DuneToolWindowFactory.ID, shouldShowDuneToolWindow(myProject));
        setToolWindowAvailable(EsyToolWindowFactory.ID, shouldShowEsyToolWindow(myProject));
    }

    private void setToolWindowAvailable(@NotNull String id, @Nullable Compiler compiler) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow(id);
        if (toolWindow != null) {
            toolWindow.setAvailable(compiler != null, () -> {
                ConsoleView consoleView = getConsoleView(id);
                if (consoleView != null && compiler != null) {
                    ReadAction.nonBlocking(() -> compiler.getFullVersion(null)) // slow operation not allowed on UI thread
                            .finishOnUiThread(ModalityState.defaultModalityState(),
                                    version -> consoleView.print("Detected compiler: " + version + "\n", ConsoleViewContentType.NORMAL_OUTPUT))
                            .submit(AppExecutorUtil.getAppExecutorService());
                }
            });
        }
    }

    private static @Nullable Compiler shouldShowBsToolWindow(@NotNull Project project) {
        Compiler compiler = project.getService(ORCompilerManager.class).getCompiler(CompilerType.BS);
        return compiler != null && compiler.isAvailable(project) ? compiler : null;
    }

    private static @Nullable Compiler shouldShowRescriptToolWindow(@NotNull Project project) {
        Compiler compiler = project.getService(ORCompilerManager.class).getCompiler(CompilerType.RESCRIPT);
        return compiler != null && compiler.isAvailable(project) ? compiler : null;
    }

    private static @Nullable Compiler shouldShowDuneToolWindow(@NotNull Project project) {
        Compiler compiler = project.getService(ORCompilerManager.class).getCompiler(CompilerType.DUNE);
        return ORProjectManager.isDuneProject(project) && compiler != null && compiler.isAvailable(project) ? compiler : null;
    }

    private static @Nullable Compiler shouldShowEsyToolWindow(@NotNull Project project) {
        Compiler compiler = project.getService(ORCompilerManager.class).getCompiler(CompilerType.ESY);
        return compiler != null && ORProjectManager.isEsyProject(project) ? compiler : null;
    }
}
