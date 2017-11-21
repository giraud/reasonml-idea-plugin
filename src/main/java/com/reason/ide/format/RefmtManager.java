package com.reason.ide.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;

public class RefmtManager {
    private static RefmtManager instance;

    private final RefmtProcess m_refmtProcess;

    private RefmtManager(RefmtProcess refmtProcess) {
        m_refmtProcess = refmtProcess;
    }

    // Bad implementation, should have synchronisation locks
    public static RefmtManager getInstance() {
        if (instance == null) {
            RefmtProcess refmtProcess = new RefmtProcess();
            try {
                instance = new RefmtManager(refmtProcess);
            } catch (Exception err) {
                Logger.getInstance("ReasonML.refmt").error("refmt: " + err.getMessage());
            }
        }

        return instance;
    }

    void refmt(Project project, String format, Document document) {
        String oldText = document.getText();
        String newText = m_refmtProcess.run(project, format, oldText);
        if (!oldText.isEmpty() && !newText.isEmpty()) { // additional protection
            document.setText(newText);
        }
    }
}
