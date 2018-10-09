package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiTypeStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiTypeImpl extends StubBasedPsiElementBase<PsiTypeStub> implements PsiType {

    //region Constructors
    public PsiTypeImpl(@NotNull ASTNode node) {
        super(node);
    }

    public PsiTypeImpl(PsiTypeStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiTypeConstrName constr = findChildByClass(PsiTypeConstrName.class);
        return constr == null ? null : PsiTreeUtil.findChildOfType(constr, PsiLowerSymbol.class);
    }

    @NotNull
    @Override
    public String getName() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            String name = stub.getName();
            return name == null ? "" : name;
        }

        PsiElement constrName = findChildByClass(PsiTypeConstrName.class);
        if (constrName == null) {
            return "";
        }

        StringBuilder nameBuilder = new StringBuilder();
        boolean first = true;

        PsiElement element = constrName.getFirstChild();
        while (element != null) {
            if (element instanceof PsiLowerSymbol) {
                if (!first) {
                    nameBuilder.append(" ");
                }
                nameBuilder.append(element.getText());
                first = false;
            }
            element = element.getNextSibling();
        }

        return nameBuilder.toString();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    @Nullable
    public PsiTypeBinding getBinding() {
        return findChildByClass(PsiTypeBinding.class);
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        String path;

        PsiElement parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = ((PsiModule) parent).getQualifiedName();
        } else {
            path = ORUtil.fileNameToModuleName(getContainingFile());
        }

        return path + "." + getName();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return Icons.TYPE;
            }
        };
    }

    @Override
    public String toString() {
        return "Type " + getQualifiedName();
    }

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
