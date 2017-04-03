package com.reason.ide;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class ReasonDocumentManager implements ApplicationComponent {

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonDocumentManager";
    }

    @Override
    public void initComponent() {
        ReformatOnSave handler = new ReformatOnSave();
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, handler);
    }

    @Override
    public void disposeComponent() {
    }
}
