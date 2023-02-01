package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.Collections.*;

public class RPsiTypeImpl extends RPsiTokenStub<ORLangTypes, RPsiType, PsiTypeStub> implements RPsiType {
    // region Constructors
    public RPsiTypeImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiTypeImpl(@NotNull ORLangTypes types, @NotNull PsiTypeStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByClass(RPsiLowerSymbol.class);
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
        PsiElement id = getNameIdentifier();
        PsiElement newId = ORCodeFactory.createLetName(getProject(), name);
        if (id != null && newId != null) {
            id.replace(newId);
        }

        return this;
    }
    // endregion

    @Override
    public @NotNull PsiElement getNavigationElement() {
        PsiElement id = getNameIdentifier();
        return id == null ? this : id;
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

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

        RPsiTypeBinding binding = getBinding();
        return binding != null && binding.getFirstChild() instanceof RPsiJsObject;
    }

    @Override
    public boolean isRecord() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.isRecord();
        }

        RPsiTypeBinding binding = getBinding();
        return binding != null && binding.getFirstChild() instanceof RPsiRecord;
    }

    @Override
    public @Nullable RPsiTypeBinding getBinding() {
        return findChildByClass(RPsiTypeBinding.class);
    }

    @Override
    public @NotNull Collection<RPsiVariantDeclaration> getVariants() {
        RPsiTypeBinding binding = getBinding();
        if (binding != null) {
            return PsiTreeUtil.findChildrenOfType(binding, RPsiVariantDeclaration.class);
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
