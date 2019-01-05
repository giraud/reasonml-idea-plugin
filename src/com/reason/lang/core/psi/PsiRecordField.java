package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORSignature;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiRecordField extends PsiToken<ORTypes> implements PsiNamedElement, PsiSignatureElement {

    public PsiRecordField(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Nullable
    public PsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
    }

    @NotNull
    @Override
    public ORSignature getHMSignature() {
        PsiSignature signature = getSignature();
        return signature == null ? ORSignature.EMPTY : signature.asHMSignature();
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Nullable
    @Override
    public String toString() {
        return "Record field " + getName();
    }

}