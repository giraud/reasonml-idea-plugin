package com.reason.ide.editors;

import com.intellij.ide.highlighter.*;
import com.intellij.lang.xhtml.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.ui.*;
import com.intellij.util.ui.components.*;
import org.jetbrains.annotations.*;

import javax.swing.event.*;

public class CmtXmlComponent extends BorderLayoutPanel implements ChangeListener {
    private final Project myProject;
    private final String myXmlDump;
    private boolean myChildrenAdded = false;

    public CmtXmlComponent(@NotNull Project project, @NotNull TabbedPaneWrapper rootTabbedPane, @NotNull String xmlDump) {
        myProject = project;
        myXmlDump = xmlDump;
        rootTabbedPane.addChangeListener(this);
    }

    @Override
    public void stateChanged(@NotNull ChangeEvent changeEvent) {
        if (!myChildrenAdded) {
            addChildren();
            myChildrenAdded = true;
        }
    }

    private void addChildren() {
        PsiFile psiFile = PsiFileFactory.getInstance(myProject).createFileFromText(XHTMLLanguage.INSTANCE, myXmlDump);
        Document document = PsiDocumentManager.getInstance(myProject).getDocument(psiFile);
        if (document != null) {
            Editor editor = EditorFactory.getInstance().createEditor(document, myProject, HtmlFileType.INSTANCE, true);
            addToCenter(editor.getComponent());
        }
    }
}
