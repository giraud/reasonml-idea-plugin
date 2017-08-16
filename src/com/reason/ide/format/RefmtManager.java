package com.reason.ide.format;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;

public class RefmtManager {
    private static RefmtManager instance;

    private final RefmtProcess m_refmtProcess;
    private final boolean m_useDoubleDash;

    private RefmtManager(RefmtProcess refmtProcess, boolean useDoubleDash) {
        m_refmtProcess = refmtProcess;
        m_useDoubleDash = useDoubleDash;
    }

    // Bad implementation, should have synchronisation locks
    public static RefmtManager getInstance() {
        if (instance == null) {
            RefmtProcess refmtProcess = new RefmtProcess();
            try {
                boolean useDoubleDash = true; // not working: refmtProcess.useDoubleDash();
                instance = new RefmtManager(refmtProcess, useDoubleDash);
            } catch (Exception err) {
                Logger.getInstance("ReasonML.refmt").error("refmt: " + err.getMessage());
            }
        }

        return instance;
    }

    void refmt(Document document) {
        if (isReasonFile(document)) {
            String oldText = document.getText();
            String newText = m_refmtProcess.run(m_useDoubleDash, oldText);
            if (!oldText.isEmpty() && !newText.isEmpty()) { // additional protection
                document.setText(newText);
            }
        }
    }

    private boolean isReasonFile(Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String extension = file == null ? null : file.getExtension();
        return "re".equals(extension) || "rei".equals(extension);
    }
}
