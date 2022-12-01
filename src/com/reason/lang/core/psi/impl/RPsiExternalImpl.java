package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class RPsiExternalImpl extends RPsiTokenStub<ORLangTypes, RPsiExternal, PsiExternalStub> implements RPsiExternal {
    // region Constructors
    public RPsiExternalImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiExternalImpl(@NotNull ORLangTypes types, @NotNull PsiExternalStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        RPsiScopedExpr operatorOverride = findChildByClass(RPsiScopedExpr.class);
        if (operatorOverride != null) {
            return operatorOverride;
        }

        return findChildByClass(RPsiLowerSymbol.class);
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
    public String @NotNull [] getPath() {
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
    public @NotNull PsiElement getNavigationElement() {
        PsiElement id = getNameIdentifier();
        return id == null ? this : id;
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @Nullable RPsiSignature getSignature() {
        return findChildByClass(RPsiSignature.class);
    }

    private @NotNull String getRealName() {
        PsiElement name = findChildByType(myTypes.STRING_VALUE);
        return name == null ? "" : name.getText();
    }

    @Override
    public boolean isFunction() {
        PsiExternalStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        RPsiSignature signature = PsiTreeUtil.findChildOfType(this, RPsiSignature.class);
        return signature != null && signature.isFunction();
    }

    @Override
    public @NotNull String getExternalName() {
        PsiElement eq = ORUtil.findImmediateFirstChildOfType(this, myTypes.EQ);
        if (eq != null) {
            PsiElement next = ORUtil.nextSiblingWithTokenType(eq, myTypes.STRING_VALUE);
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
                RPsiSignature signature = getSignature();
                return signature == null ? null : signature.asText(ORLanguageProperties.cast(getLanguage()));
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ORIcons.EXTERNAL;
            }
        };
    }
}
