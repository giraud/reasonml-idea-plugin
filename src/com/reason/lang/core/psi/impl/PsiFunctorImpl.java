package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiFunctorImpl extends PsiToken<ORTypes> implements PsiFunctor {

    public PsiFunctorImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        /*
        PsiFunctorStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }
        */
        return ORUtil.getQualifiedName(this);
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Nullable
    @Override
    public PsiFunctorBinding getBinding() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorBinding.class);
    }

    @NotNull
    @Override
    public Collection<PsiParameter> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class), PsiParameter.class);
    }

    @Nullable
    @Override
    public PsiElement getReturnType() {
        return null;
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.FUNCTOR;
            }
        };
    }

    @Override
    public String toString() {
        return "Functor";
    }
}
