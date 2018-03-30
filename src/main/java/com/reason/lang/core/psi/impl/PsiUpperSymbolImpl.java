package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.lang.MlTypes;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiUpperSymbolImpl extends MlAstWrapperPsiElement implements PsiUpperSymbol {

    //region Constructors
    public PsiUpperSymbolImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? RmlPsiUtil.fileNameToModuleName(getContainingFile()) : nameElement.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this; // Use PsiUpperSymbolReference.handleElementRename()
    }
    //endregion

    @Override
    public boolean isVariant() {
        PsiElement firstChild = getFirstChild();
        return firstChild != null && firstChild.getNode().getElementType() == m_types.VARIANT_NAME;
    }

    @Override
    public PsiReference getReference() {
        return new PsiUpperSymbolReference(this, m_types);
    }

    @NotNull
    public String getQualifiedName() {
        return "";

        //// use a stub ?
        //String path = null;
        //
        //PsiElement parent = getParent();
        //if (parent instanceof PsiModule) {
        //    path = ((PsiModule) parent).getQualifiedName();
        //}
        //
        //if (path == null) {
        //    path = RmlPsiUtil.fileNameToModuleName(getContainingFile());
        //}
        //
        //System.out.println(" » qn uppersymbol: " + path + "." + getName());
        //return path + "." + getName();
    }

    @Override
    public String toString() {
        String name = getName();
        return "Upper symbol " + (name == null || name.isEmpty() ? "<" + ((FileBase) getContainingFile()).asModuleName() + ">" : name);
    }
}
