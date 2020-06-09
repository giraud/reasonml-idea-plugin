package com.reason.ide.intentions;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.RmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractBaseIntention<T extends PsiElement> implements IntentionAction {

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull Editor editor, PsiFile file) {
        if (file instanceof RmlFile) {
            T parentAtCaret = getParentAtCaret(editor, file);
            return parentAtCaret != null && isAvailable(project, parentAtCaret);
        }
        return false;
    }

    @NotNull
    abstract Class<T> getClazz();

    abstract boolean isAvailable(@NotNull Project project, @NotNull T parentElement);

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) throws IncorrectOperationException {
        if (!FileModificationService.getInstance().prepareFileForWrite(file)) {
            return;
        }

        PsiDocumentManager.getInstance(project).commitAllDocuments();
        T parentAtCaret = getParentAtCaret(editor, file);
        if (parentAtCaret != null) {
            ApplicationManager.getApplication().runWriteAction(() -> runInvoke(project, parentAtCaret));
        }
    }

    abstract void runInvoke(@NotNull Project project, @NotNull T parentElement);

    @Nullable
    private T getParentAtCaret(@NotNull Editor editor, @NotNull PsiFile file) {
        PsiElement element = elementAtCaret(editor, file);
        return element == null ? null : PsiTreeUtil.getParentOfType(element, getClazz());
    }

    @Nullable
    private PsiElement elementAtCaret(@NotNull final Editor editor, @NotNull final PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        return file.findElementAt(offset);
    }

}
