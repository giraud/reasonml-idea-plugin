package com.reason.ide.testAssistant;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.awt.RelativePoint;
import com.reason.lang.core.psi.PsiFunction;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.util.List;

public class TestDataNavigationHandler implements GutterIconNavigationHandler<PsiFunction> {

    static void navigate(@NotNull RelativePoint point, @NotNull List<String> paths, @NotNull Project project) {
        if (!paths.isEmpty()) {
            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(paths.get(0));
            if (virtualFile != null) {
                new OpenFileDescriptor(project, virtualFile).navigate(true);
            }
        }
    }

    @Override
    public void navigate(MouseEvent e, PsiFunction element) {
    }
}
