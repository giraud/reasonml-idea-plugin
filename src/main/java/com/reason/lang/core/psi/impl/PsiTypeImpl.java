package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.MlTypes;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.stub.PsiTypeStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiTypeImpl extends StubBasedPsiElementBase<PsiTypeStub> implements PsiType {

    private final MlTypes m_types;

    //region Constructors
    public PsiTypeImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiTypeImpl(@NotNull MlTypes types, PsiTypeStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
        m_types = types;
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiLowerSymbol.class);
    }

    @NotNull
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
    public PsiScopedExpr getScopedExpression() {
        return findChildByClass(PsiScopedExpr.class);
    }

    @NotNull
    @Override
    public String getTypeInfo() {
        return "";
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        String path;

        PsiElement parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = ((PsiModule) parent).getQualifiedName();
        } else {
            path = RmlPsiUtil.fileNameToModuleName(getContainingFile());
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
        return "Type(" + getName() + ")";
    }
}
