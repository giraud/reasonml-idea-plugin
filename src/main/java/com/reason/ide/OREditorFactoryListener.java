package com.reason.ide;

import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class OREditorFactoryListener implements EditorFactoryListener {
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        // Ensure that editor tracker is created and loaded
        Project project = event.getEditor().getProject();
        if (project != null) {
            project.getService(OREditorTracker.class);
        }
    }
}
