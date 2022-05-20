package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.Collections.*;

public class PsiTypeImpl extends PsiTokenStub<ORTypes, PsiType, PsiTypeStub> implements PsiType {
    // region Constructors
    public PsiTypeImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiTypeImpl(@NotNull ORTypes types, @NotNull PsiTypeStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByClass(PsiLowerIdentifier.class);
    }

    @Override
    public @Nullable String getName() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement constrName = getNameIdentifier();
        return constrName == null ? "" : constrName.getText();

    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedPathName
    @Override
    public @Nullable String[] getPath() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public boolean isAbstract() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.isAbstract();
        }

        return getBinding() == null;
    }

    @Override
    public boolean isJsObject() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.isJsObject();
        }

        PsiTypeBinding binding = getBinding();
        return binding != null && binding.getFirstChild() instanceof PsiJsObject;
    }

    @Override
    public boolean isRecord() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.isRecord();
        }

        PsiTypeBinding binding = getBinding();
        return binding != null && binding.getFirstChild() instanceof PsiRecord;
    }

    @Override
    public @Nullable PsiTypeBinding getBinding() {
        return findChildByClass(PsiTypeBinding.class);
    }

    @Override
    public @NotNull Collection<PsiVariantDeclaration> getVariants() {
        PsiTypeBinding binding = getBinding();
        if (binding != null) {
            return PsiTreeUtil.findChildrenOfType(binding, PsiVariantDeclaration.class);
        }
        return emptyList();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Override
            public @Nullable String getLocationString() {
                return null;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.TYPE;
            }
        };
    }
}
