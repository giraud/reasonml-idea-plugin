package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PsiRecord extends ASTWrapperPsiElement {

    public PsiRecord(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @NotNull
    public Collection<PsiRecordField> getFields() {
        return PsiTreeUtil.findChildrenOfType(this, PsiRecordField.class);
    }

    @Override
    public String toString() {
        return "Record";
    }
}