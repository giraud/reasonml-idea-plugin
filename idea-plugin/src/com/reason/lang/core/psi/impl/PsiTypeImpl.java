package com.reason.lang.core.psi.impl;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeBinding;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.stub.PsiTypeStub;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;

import static java.util.Collections.*;

public class PsiTypeImpl extends PsiTokenStub<ORTypes, PsiTypeStub> implements PsiType {

    //region Constructors
    public PsiTypeImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiTypeImpl(@NotNull ORTypes types, @NotNull PsiTypeStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    //region PsiNamedElement
    @NotNull
    @Override
    public String getName() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            String name = stub.getName();
            return name == null ? "" : name;
        }

        PsiElement constrName = findChildByClass(PsiLowerIdentifier.class);
        if (constrName == null) {
            return "";
        }

        /* zzz
        PsiParameters parameters = findChildByClass(PsiParameters.class);

        StringBuilder nameBuilder = new StringBuilder();
        boolean first = true;

        PsiElement element = constrName.getFirstChild();
        while (element != null) {
            if (element instanceof PsiLowerIdentifier) {
                if (!first) {
                    nameBuilder.append(" ");
                }
                nameBuilder.append(element.getText());
                first = false;
            }
            element = element.getNextSibling();
        }

        return nameBuilder.toString();

         */
        return constrName.getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("use lower identifier");
    }
    //endregion

    @Override
    public boolean isAbstract() {
        return getBinding() == null;
    }

    @Override
    @Nullable
    public PsiTypeBinding getBinding() {
        return findChildByClass(PsiTypeBinding.class);
    }

    @NotNull
    @Override
    public Collection<PsiVariantDeclaration> getVariants() {
        PsiTypeBinding binding = getBinding();
        if (binding != null) {
            return PsiTreeUtil.findChildrenOfType(binding, PsiVariantDeclaration.class);
        }
        return emptyList();
    }

    @NotNull
    @Override
    public String getPath() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiTypeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
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
                return ORIcons.TYPE;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "Type " + getQualifiedName();
    }
}
