package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.MlTypes;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class PsiExternalImpl extends MlAstWrapperPsiElement implements PsiExternal {

    //region Constructors
    public PsiExternalImpl(MlTypes types, ASTNode node) {
        super(types, node);
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

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier == null) {
            return "unknown";
        }

        if (nameIdentifier instanceof PsiScopedExpr) {
            String text = nameIdentifier.getText();
            return text.substring(1, text.length() - 2).trim();
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

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.EXTERNAL;
            }
        };
    }
}
