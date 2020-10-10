package com.reason.ide;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

@Service
public final class OREditorTracker implements Disposable {

    @NotNull
    private final ORFileEditorListener m_fileEditorListener;

    public OREditorTracker(@NotNull Project project) {
        m_fileEditorListener = new ORFileEditorListener(project);
        project
                .getMessageBus()
                .connect(this)
                .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, m_fileEditorListener);
    }

    @Override
    public void dispose() {
    }

    public void updateQueues() {
        m_fileEditorListener.updateQueues();
    }

    static final class AppLevelEditorFactoryListener implements EditorFactoryListener {
        @Override
        public void editorCreated(@NotNull EditorFactoryEvent event) {
            Project project = event.getEditor().getProject();
            if (project != null) {
                ServiceManager.getService(project, OREditorTracker.class);
            }
        }

        @Override
        public void editorReleased(@NotNull EditorFactoryEvent event) {
            Project project = event.getEditor().getProject();
            if (project != null) {
                ServiceManager.getService(project, OREditorTracker.class);
            }
        }
    }
}
