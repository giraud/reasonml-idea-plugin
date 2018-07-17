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
import com.reason.lang.core.psi.type.MlTypes;
import com.reason.lang.core.stub.PsiExternalStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class PsiExternalImpl extends StubBasedPsiElementBase<PsiExternalStub> implements PsiExternal {

    @NotNull
    private final MlTypes m_types;

    //region Constructors
    public PsiExternalImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiExternalImpl(@NotNull MlTypes types, @NotNull PsiExternalStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
        m_types = types;
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        PsiScopedExpr operatorOverride = findChildByClass(PsiScopedExpr.class);
        if (operatorOverride != null) {
            return operatorOverride;
        }

        return findChildByClass(PsiLowerSymbol.class);
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        String path;

        PsiElement parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = ((PsiModule) parent).getQualifiedName();
        } else {
            path = PsiUtil.fileNameToModuleName(getContainingFile());
        }

        return path + "." + getName();
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return "unknown";
        }

        if (nameIdentifier instanceof PsiScopedExpr) {
            String text = nameIdentifier.getText();
            int endIndex = text.length() - 2;
            return endIndex <= 1 ? "" : text.substring(1, endIndex).trim();
        }

        return nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @NotNull
    @Override
    public HMSignature getSignature() {
        PsiSignature signature = findChildByClass(PsiSignature.class);
        return signature == null ? HMSignature.EMPTY : signature.asHMSignature();
    }

    private String getRealName() {
        PsiElement name = findChildByType(m_types.STRING);
        return name == null ? "" : name.getText();
    }

    @Override
    public boolean isFunction() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        PsiSignature signature = PsiTreeUtil.findChildOfType(this, PsiSignature.class);
        return signature != null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                String aliasName = getName();

                String realName = getRealName();
                if (!realName.isEmpty()) {
                    String realNameText = realName.substring(1, realName.length() - 1);
                    if (!Objects.equals(aliasName, realNameText)) {
                        aliasName += " (" + realNameText + ")";
                    }
                }

                String signature = getSignature().toString();
                if (!signature.isEmpty()) {
                    aliasName += " :  " + signature;
                }

                return aliasName;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return Icons.EXTERNAL;
            }
        };
    }

    @Override
    public String toString() {
        return "External" + (isFunction() ? ".f " : " ") + getQualifiedName();
    }
}
