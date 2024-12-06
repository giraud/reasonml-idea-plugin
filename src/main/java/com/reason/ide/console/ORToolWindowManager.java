package com.reason.ide.console;

import com.intellij.execution.ui.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.*;
import com.intellij.util.concurrency.*;
import com.reason.comp.*;
import com.reason.comp.ORCompiler.*;
import com.reason.ide.console.bs.*;
import com.reason.ide.console.dune.*;
import com.reason.ide.console.esy.*;
import com.reason.ide.console.rescript.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.concurrent.*;

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
        ExecutorService executorService = AppExecutorUtil.getAppExecutorService();
        ModalityState modalityState = ModalityState.defaultModalityState();

        ReadAction.nonBlocking(() -> {
                    ORCompiler[] compilers = new ORCompiler[4];
                    compilers[0] = getAvailableCompiler(CompilerType.RESCRIPT);
                    compilers[1] = getAvailableCompiler(CompilerType.BS);
                    compilers[2] = getAvailableCompiler(CompilerType.DUNE);
                    compilers[3] = getAvailableCompiler(CompilerType.ESY);
                    return compilers;
                })
                .finishOnUiThread(modalityState, compilers -> {
                    setToolWindowAvailable(RescriptToolWindowFactory.ID, compilers[0]);
                    setToolWindowAvailable(BsToolWindowFactory.ID, compilers[1]);
                    setToolWindowAvailable(DuneToolWindowFactory.ID, compilers[2]);
                    setToolWindowAvailable(EsyToolWindowFactory.ID, compilers[3]);
                })
                .coalesceBy(this)
                .submit(executorService);
    }

    private void setToolWindowAvailable(@NotNull String id, @Nullable ORCompiler compiler) {
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

    private @Nullable ORCompiler getAvailableCompiler(@NotNull CompilerType compilerType) {
        ORCompilerManager compilerService = myProject.isDisposed() ? null : myProject.getService(ORCompilerManager.class);
        ORCompiler compiler = compilerService != null ? compilerService.getCompiler(compilerType) : null;
        return compiler != null && compiler.isAvailable(myProject) ? compiler : null;
    }
}
