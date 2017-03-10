package com.reason.ide;


import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReformatDocumentsAdapter extends AbstractProjectComponent {
    public ReformatDocumentsAdapter(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerAdapter() {
            @Override
            public void beforeDocumentSaving(@NotNull Document document) {
                String basePath = myProject.getBasePath();
                String refmtPath = basePath + "/node_modules/bs-platform/bin";
                String refmtBin = refmtPath + "/refmt.exe";
                // reformat code using refmt tool
                ProcessBuilder pb = new ProcessBuilder(refmtBin);
                Process refmt = null;
                try {
                    refmt = pb.start();
                    if (refmt.waitFor(100, TimeUnit.MILLISECONDS)) {
                        //DONE
                        //document.setText(document.getText());
                    } else {
                        //NOK
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (refmt != null && refmt.isAlive()) {
                        refmt.destroyForcibly();
                    }
                }
            }
        });
    }
}
