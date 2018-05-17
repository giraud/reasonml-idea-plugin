package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiTypeConstrNameReference extends PsiReferenceBase<PsiTypeConstrName> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final MlTypes m_types;

    PsiTypeConstrNameReference(@NotNull PsiTypeConstrName element, @NotNull MlTypes types) {
        super(element, PsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createTypeName(myElement.getProject(), newName);
        ASTNode newNameNode = newNameIdentifier.getFirstChild().getNode();

        PsiElement nameIdentifier = myElement.getNameIdentifier();
        if (nameIdentifier == null) {
            myElement.getNode().addChild(newNameNode);
        } else {
            ASTNode oldNameNode = nameIdentifier.getNode();
            myElement.getNode().replaceChild(oldNameNode, newNameNode);
        }

        return myElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (m_referenceName == null) {
            return null;
        }

        // From the definition of a module
        PsiType parent = PsiTreeUtil.getParentOfType(myElement, PsiType.class);
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return myElement;
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
