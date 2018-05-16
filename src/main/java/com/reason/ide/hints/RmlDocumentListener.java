package com.reason.ide.hints;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class RmlDocumentListener implements DocumentListener {

    private final Project m_project;
    private int m_oldLinesCount;

    public RmlDocumentListener(Project project) {
        m_project = project;
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
        Document document = event.getDocument();
        m_oldLinesCount = document.getLineCount();
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        // When document lines count change, we clear the type annotations
        Document document = event.getDocument();
        int newLineCount = document.getLineCount();
        if (newLineCount != m_oldLinesCount) {
            CodeLensView.CodeLensInfo userData = m_project.getUserData(CodeLensView.CODE_LENS);
            if (userData != null) {
                VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                if (file != null) {
                    int startLine = document.getLineNumber(event.getOffset());
                    int direction = newLineCount - m_oldLinesCount;
                    userData.move(file, startLine, direction, file.getTimeStamp());
                }
            }
        }
    }

}
