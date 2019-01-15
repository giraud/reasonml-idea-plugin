package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiExternalStub;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class PsiExternalImpl extends PsiTokenStub<ORTypes, PsiExternalStub> implements PsiExternal {

    //region Constructors
    public PsiExternalImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiExternalImpl(@NotNull ORTypes types, @NotNull PsiExternalStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
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

        PsiModule parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = parent.getQualifiedName();
        } else {
            path = ORUtil.fileNameToModuleName(getContainingFile());
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

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion


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

    @NotNull
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
        return signature != null && signature.asHMSignature().isFunctionSignature();
    }

    @NotNull
    @Override
    public String getExternalName() {
        ASTNode eqNode = getNode().findChildByType(m_types.EQ);
        if (eqNode != null) {
            ASTNode nextNode = ORUtil.nextSiblingNode(eqNode);
            if (nextNode.getElementType() == m_types.STRING_VALUE) {
                String text = nextNode.getText();
                return 2 < text.length() ? text.substring(1, text.length() - 1) : "";
            }
        }
        return "";
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

                String signature = getORSignature().asString(getLanguage());
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

    @Nullable
    @Override
    public String toString() {
        return "External" + (isFunction() ? ".f " : " ") + getQualifiedName();
    }

    //region Compatibility
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
