package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiLowerSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiLowerSymbolImpl extends MlAstWrapperPsiElement implements PsiLowerSymbol {

    //region Constructors
    public PsiLowerSymbolImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    public PsiReference getReference() {
        return new PsiLowerSymbolReference(this, m_types);
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        String path = null;

        //PsiElement parent = getParent();
        //if (parent instanceof PsiQualifiedNamedElement) {
        //    path = ((PsiQualifiedNamedElement) parent).getQualifiedName();
        //} else {
        //    PsiElement prevSibling = getPrevSibling();
        //    if (prevSibling.getNode().getElementType() == m_types.DOT) {
        //        PsiElement dotPrevSibling = prevSibling.getPrevSibling();
        //        if (dotPrevSibling instanceof PsiQualifiedNamedElement) {
        //            path = ((PsiQualifiedNamedElement) dotPrevSibling).getQualifiedName();
        //        }
        //    } else {
        //        PsiModule module = PsiTreeUtil.getParentOfType(this, PsiModule.class);
        //        if (module != null) {
        //            path = module.getQualifiedName();
        //        }
        //    }
        //}

        if (path == null) {
            path = PsiUtil.fileNameToModuleName(getContainingFile());
        }

        return path + "." + getName();
    }

    @Override
    public String toString() {
        return "Lower symbol " + getName();
    }
}
