package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class PsiRecordField extends ASTWrapperPsiElement {

    public PsiRecordField(ASTNode node) {
        super(node);
    }

    @Nullable
    private PsiElement getNameElement() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameElement();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    public PsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public String toString() {
        return "Record field " + getName();
    }
}