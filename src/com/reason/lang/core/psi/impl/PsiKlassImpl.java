package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class PsiKlassImpl extends PsiTokenStub<ORTypes, PsiKlass, PsiKlassStub> implements PsiKlass {
    // region Constructors
    public PsiKlassImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiKlassImpl(@NotNull ORTypes types, @NotNull PsiKlassStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByClass(PsiLowerSymbol.class);
    }

    @Override
    public @Nullable String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion


    //region PsiQualifiedName
    @Override
    public @Nullable String[] getPath() {
        PsiKlassStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiKlassStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public @Nullable PsiElement getClassBody() {
        return PsiTreeUtil.findChildOfType(this, PsiObject.class);
    }

    @Override
    public @NotNull Collection<PsiClassField> getFields() {
        return PsiTreeUtil.findChildrenOfType(getClassBody(), PsiClassField.class);
    }

    @Override
    public @NotNull Collection<PsiClassMethod> getMethods() {
        return PsiTreeUtil.findChildrenOfType(getClassBody(), PsiClassMethod.class);
    }

    @Override
    public @NotNull Collection<PsiParameters> getParameters() {
        return PsiTreeUtil.findChildrenOfType(this, PsiParameters.class);
    }

    @Override
    public @Nullable PsiClassConstructor getConstructor() {
        return findChildByClass(PsiClassConstructor.class);
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                return getName();
            }

            @Override
            public @Nullable String getLocationString() {
                return null;
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.CLASS;
            }
        };
    }
}
