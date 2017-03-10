package com.reason.ide;


import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class ReformatDocumentsAdapter extends AbstractProjectComponent {
    public ReformatDocumentsAdapter(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentManagerAdapter() {
            /**
             * On save, reformat code using remft tool.
             * This method is only working on linux for now, refmt doesn't seem to work on my windows 64bits.
             * Might need more optimisation in the future, and could be a service like merlin (don't create processes
             * each time ?).
             *
             * @param document Document that is being saved
             */
            @Override
            public void beforeDocumentSaving(@NotNull Document document) {
                ProcessBuilder processBuilder = new ProcessBuilder(getRefmtBinary());
                Process refmt = null;
                try {
                    refmt = processBuilder.start();
                    if (refmt.waitFor(300, TimeUnit.MILLISECONDS)) {
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream()));
                        BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream()));

                        String text = document.getText();

                        writer.write(text);
                        writer.flush();
                        writer.close();

                        StringBuffer sb = new StringBuffer(text.length());
                        reader.lines().forEach(s1 -> sb.append(s1).append(System.lineSeparator()));
                        document.setText(sb.toString());
                    }
                    // else {
                        // TODO something went wrong
                    // }
                } catch (IOException | InterruptedException | RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    if (refmt != null && refmt.isAlive()) {
                        refmt.destroyForcibly();
                    }
                }
            }
        });
    }

    @NotNull
    private String getRefmtBinary() {
        String basePath = myProject.getBasePath();
        String refmtPath = basePath + "/node_modules/bs-platform/bin";
        return refmtPath + "/refmt.exe";
    }
}
