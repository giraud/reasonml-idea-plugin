package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiValStub;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiValImpl extends StubBasedPsiElementBase<PsiValStub> implements PsiVal {

    @NotNull
    private final ORTypes m_types;

    //region Constructors
    public PsiValImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiValImpl(@NotNull ORTypes types, @NotNull PsiValStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
        m_types = types;
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Collection<PsiElement> elements = PsiTreeUtil.findChildrenOfAnyType(this, PsiLowerSymbol.class, PsiScopedExpr.class);
        return elements.isEmpty() ? null : elements.iterator().next();
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion


    @Nullable
    @Override
    public String getQualifiedName() {
        String path;

        PsiElement parent = PsiTreeUtil.getStubOrPsiParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = ((PsiModule) parent).getQualifiedName();
        } else {
            path = PsiUtil.fileNameToModuleName(getContainingFile());
        }

        return path + "." + getName();
    }

    @NotNull
    @Override
    public HMSignature getSignature() {
        PsiSignature signature = findChildByClass(PsiSignature.class);
        return signature == null ? HMSignature.EMPTY : signature.asHMSignature();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName() + ": " + getSignature();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.VAL;
            }
        };
    }

    @Override
    public String toString() {
        return "Val " + getQualifiedName();
    }
}
