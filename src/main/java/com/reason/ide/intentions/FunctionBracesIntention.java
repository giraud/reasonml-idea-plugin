package com.reason.ide.intentions;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.impl.RmlElementFactory;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class FunctionBracesIntention implements IntentionAction {
    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add braces to arrow function";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "reason";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (file instanceof RmlFile) {
            PsiLet let = getLetAtCaret(editor, file);
            if (let != null && let.isFunction()) {
                PsiLetBinding binding = let.getBinding();
                if (binding != null) {
                    PsiElement firstChild = binding.getFirstChild();
                    ASTNode childNode = firstChild.getNode();
                    return childNode.getElementType() != RmlTypes.INSTANCE.LBRACE;
                }
            }
        }

        return false;
    }

    private PsiLet getLetAtCaret(Editor editor, PsiFile file) {
        PsiElement element = elementAtCaret(editor, file);
        if (element != null) {
            return PsiTreeUtil.getParentOfType(element, PsiLet.class);
        }

        return null;
    }

    private PsiElement elementAtCaret(final Editor editor, final PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        return file.findElementAt(offset);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (!FileModificationService.getInstance().prepareFileForWrite(file)) return;

        PsiDocumentManager.getInstance(project).commitAllDocuments();

        PsiLet let = getLetAtCaret(editor, file);
        PsiLetBinding binding = let == null ? null : let.getBinding();
        if (binding != null) {
            PsiLet newLet = (PsiLet) RmlElementFactory.createExpression(project, "let x = {\n  " + binding.getText() + "\n};");
            PsiLetBinding newBinding = newLet == null ? null : newLet.getBinding();
            if (newBinding != null) {
                ASTNode oldBindingNode = binding.getNode();
                ApplicationManager.getApplication().runWriteAction(() -> {
                    let.getNode().replaceChild(oldBindingNode, newBinding.getNode());
                });
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
