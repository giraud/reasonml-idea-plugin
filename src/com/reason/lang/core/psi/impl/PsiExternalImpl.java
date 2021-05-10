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

public class PsiExternalImpl extends PsiTokenStub<ORTypes, PsiExternal, PsiExternalStub> implements PsiExternal {
    // region Constructors
    public PsiExternalImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiExternalImpl(@NotNull ORTypes types, @NotNull PsiExternalStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        PsiScopedExpr operatorOverride = findChildByClass(PsiScopedExpr.class);
        if (operatorOverride != null) {
            return operatorOverride;
        }

        return findChildByClass(PsiLowerIdentifier.class);
    }

    @Override
    public String getName() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return "unknown";
        }

        return nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedName
    @Override
    public @NotNull String[] getPath() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public @Nullable PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    private @NotNull String getRealName() {
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
        return signature != null && signature.isFunction();
    }

    @Override
    public @NotNull String getExternalName() {
        PsiElement eq = ORUtil.findImmediateFirstChildOfType(this, m_types.EQ);
        if (eq != null) {
            PsiElement next = ORUtil.nextSiblingWithTokenType(eq, m_types.STRING_VALUE);
            if (next != null) {
                String text = next.getText();
                return 2 < text.length() ? text.substring(1, text.length() - 1) : "";
            }
        }
        return "";
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
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

            @Override
            public @Nullable String getLocationString() {
                PsiSignature signature = getSignature();
                return signature == null ? null : signature.asText(getLanguage());
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.EXTERNAL;
            }
        };
    }

    @Override
    public @Nullable String toString() {
        return "external " + getQualifiedName();
    }
}
