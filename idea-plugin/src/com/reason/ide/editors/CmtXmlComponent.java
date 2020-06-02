package com.reason.ide.editors;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lang.xhtml.XHTMLLanguage;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CmtXmlComponent extends BorderLayoutPanel implements ChangeListener {

    private final Project m_project;
    private final String m_xmlDump;
    private boolean childrenAdded = false;

    public CmtXmlComponent(@NotNull Project project, @NotNull JBTabbedPane rootTabbedPane, @NotNull String xmlDump) {
        m_project = project;
        m_xmlDump = xmlDump;
        rootTabbedPane.addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        Object source = changeEvent.getSource();
        if (source instanceof JBTabbedPane) {
            if (((JBTabbedPane) source).getSelectedComponent() == this) {
                ensureChildrenAdded();
            }
        }
    }

    private void ensureChildrenAdded() {
        if (!childrenAdded) {
            addChildren();
            childrenAdded = true;
        }
    }

    private void addChildren() {
        PsiFile psiFile = PsiFileFactory.getInstance(m_project).createFileFromText(XHTMLLanguage.INSTANCE, m_xmlDump);
        Document document = PsiDocumentManager.getInstance(m_project).getDocument(psiFile);
        if (document != null) {
            Editor editor = EditorFactory.getInstance().createEditor(document, m_project, HtmlFileType.INSTANCE, true);
            addToCenter(editor.getComponent());
        }
    }
}
