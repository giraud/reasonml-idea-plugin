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
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.stub.PsiExternalStub;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;

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
    public PsiElement getNameIdentifier() {
        PsiScopedExpr operatorOverride = findChildByClass(PsiScopedExpr.class);
        if (operatorOverride != null) {
            return operatorOverride;
        }

        return findChildByClass(PsiLowerIdentifier.class);
    }

    @NotNull
    @Override
    public String getPath() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return "unknown";
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
        PsiElement name = findChildByType(m_types.STRING_VALUE);
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

                return aliasName;
            }

            @Nullable
            @Override
            public String getLocationString() {
                String signature = getORSignature().asString(getLanguage());
                if (!signature.isEmpty()) {
                    return signature;
                }
                return null;
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.EXTERNAL;
            }
        };
    }

    @Nullable
    @Override
    public String toString() {
        return "external " + getQualifiedName();
    }
}
