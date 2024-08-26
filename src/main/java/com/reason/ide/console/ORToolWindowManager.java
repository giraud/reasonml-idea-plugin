package com.reason.ide.console;

import com.intellij.execution.ui.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.intellij.util.concurrency.*;
import com.reason.comp.Compiler;
import com.reason.comp.Compiler.*;
import com.reason.comp.*;
import com.reason.ide.console.bs.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.console.esy.*;
import com.reason.ide.console.rescript.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

@Service(Service.Level.PROJECT)
public final class ORToolWindowManager {
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

    public void shouldShowToolWindows() {
        setToolWindowAvailable(RescriptToolWindowFactory.ID, getAvailableCompiler(CompilerType.RESCRIPT));
        setToolWindowAvailable(BsToolWindowFactory.ID, getAvailableCompiler(CompilerType.BS));
        setToolWindowAvailable(DuneToolWindowFactory.ID, getAvailableCompiler(CompilerType.DUNE));
        setToolWindowAvailable(EsyToolWindowFactory.ID, getAvailableCompiler(CompilerType.ESY));
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
                            .coalesceBy(compiler)
                            .submit(AppExecutorUtil.getAppExecutorService());
                }
            });
        }
    }

    private @Nullable Compiler getAvailableCompiler(@NotNull CompilerType compilerType) {
        ORCompilerManager compilerService = myProject.isDisposed() ? null : myProject.getService(ORCompilerManager.class);
        Compiler compiler = compilerService != null ? compilerService.getCompiler(compilerType) : null;
        return compiler != null && compiler.isAvailable(myProject) ? compiler : null;
    }
}
