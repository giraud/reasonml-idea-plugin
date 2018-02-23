package com.reason.ide.docs;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.Nullable;

public class DocumentationProvider extends AbstractDocumentationProvider {

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof PsiUpperSymbol) {
            element = element.getParent();
            PsiElement previousElement = element == null ? null : PsiTreeUtil.prevVisibleLeaf(element);
            if (previousElement instanceof PsiComment) {
                return previousElement.getText();
            }
        } else if (element instanceof PsiVal) {
            PsiElement previousElement = PsiTreeUtil.prevVisibleLeaf(element);
            if (previousElement instanceof PsiComment) {
                String commentText = previousElement.getText();
                return commentText.substring(3/* (** */, commentText.length() - 2 /* *) */).trim();
            }
        }

        return super.generateDoc(element, originalElement);
    }


}
