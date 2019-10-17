package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.stub.PsiValStub;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiValImpl extends PsiTokenStub<ORTypes, PsiValStub> implements PsiVal {

    //region Constructors
    public PsiValImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiValImpl(@NotNull ORTypes types, @NotNull PsiValStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        Collection<PsiElement> elements = PsiTreeUtil.findChildrenOfAnyType(this, PsiLowerSymbol.class, PsiScopedExpr.class);
        return elements.isEmpty() ? null : elements.iterator().next();
    }

    @NotNull
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    public boolean isFunction() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        PsiSignature signature = getPsiSignature();
        if (signature != null) {
            return signature.asHMSignature().isFunctionSignature();
        }
        return false;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        PsiValStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }
        return ORUtil.getQualifiedName(this);
    }

    @Nullable
    @Override
    public PsiSignature getPsiSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @NotNull
    @Override
    public ORSignature getORSignature() {
        PsiSignature signature = getPsiSignature();
        return signature == null ? ORSignature.EMPTY : signature.asHMSignature();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                String valName = getName();

                ORSignature signature = getORSignature();
                if (!signature.isEmpty()) {
                    valName += ": " + signature.asString(getLanguage());
                }

                return valName;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.VAL;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "Val " + getQualifiedName();
    }

}
