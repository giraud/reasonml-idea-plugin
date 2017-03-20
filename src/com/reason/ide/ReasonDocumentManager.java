package com.reason.ide;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class ReasonDocumentManager implements ApplicationComponent /*extends AbstractProjectComponent*/ {
    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonDocumentManager";
    }

    @Override
    public void initComponent() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, new ReformatOnSave(getRefmtBinary()));
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    private String getRefmtBinary() {
        String home = System.getProperty("user.home");
        String path = home + "/.nvm/versions/node/v6.10.0/bin"; // temporary
        return path + "/refmt";
        // String basePath = myProject.getBasePath();
        // String refmtPath = basePath + "/node_modules/bs-platform/bin";
        // return refmtPath + "/refmt.exe";
    }
}
