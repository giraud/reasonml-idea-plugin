package com.reason.ide.testAssistant;

import com.intellij.codeInsight.daemon.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.ui.awt.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.awt.event.*;
import java.util.*;

public class TestDataNavigationHandler implements GutterIconNavigationHandler<RPsiFunction> {

    static void navigate(
            @NotNull RelativePoint point, @NotNull List<String> paths, @NotNull Project project) {
        if (!paths.isEmpty()) {
            VirtualFile virtualFile =
                    LocalFileSystem.getInstance().refreshAndFindFileByPath(paths.get(0));
            if (virtualFile != null) {
                new OpenFileDescriptor(project, virtualFile).navigate(true);
            }
        }
    }

    @Override
    public void navigate(MouseEvent e, RPsiFunction element) {
    }
}
