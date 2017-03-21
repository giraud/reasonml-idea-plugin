package com.reason.ide;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class ReasonDocumentManager implements ApplicationComponent /*extends AbstractProjectComponent*/ {
    private final boolean useWin = false;

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonDocumentManager";
    }

    @Override
    public void initComponent() {
        ReformatOnSave handler = new ReformatOnSave(getRefmtBinary(), this.useWin);
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, handler);
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    private String getRefmtBinary() {
        // Temporary hack for win, need env var set correctly
        String home = this.useWin ? "/home/hgiraud" : System.getProperty("user.home");
        return home + "/.nvm/versions/node/v6.10.0/bin/refmt";
        // String basePath = myProject.getBasePath();
        // String refmtPath = basePath + "/node_modules/bs-platform/bin";
        // return refmtPath + "/refmt.exe";
    }
}
