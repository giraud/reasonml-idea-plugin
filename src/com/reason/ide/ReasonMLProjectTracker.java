package com.reason.ide;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.reason.ide.hints.ReasonMLDocumentListener;

public class ReasonMLProjectTracker extends AbstractProjectComponent {

    private ReasonMLDocumentListener documentListener;

    protected ReasonMLProjectTracker(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        this.documentListener = new ReasonMLDocumentListener(myProject);
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(this.documentListener);
    }

    @Override
    public void projectClosed() {
        this.documentListener.projectClosed();
        EditorFactory.getInstance().getEventMulticaster().removeDocumentListener(this.documentListener);
    }
}
