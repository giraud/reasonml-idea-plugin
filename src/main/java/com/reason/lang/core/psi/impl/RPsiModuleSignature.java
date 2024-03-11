package com.reason.lang.core.psi.impl;

import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.Collections.*;

/*
 module M : Signature = ...
 module M : { /...signature/ } = ...
 */
public class RPsiModuleSignature extends ORCompositePsiElement<ORLangTypes> implements RPsiSignature {
    public static final String[] EMPTY_PATH = new String[0];

    protected RPsiModuleSignature(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiUpperSymbol getNameIdentifier() {
        if (myTypes == ResTypes.INSTANCE) {
            RPsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, RPsiScopedExpr.class);
            PsiElement rootElement = scope != null ? scope : this;
            return ORUtil.findImmediateLastChildOfClass(rootElement, RPsiUpperSymbol.class);
        }
        return ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public @Nullable String getName() { // used when default usage
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier != null ? nameIdentifier.getText() : null;
    }

    public @NotNull String getQName() { // used when first class module signature
        String text;
        if (myTypes == ResTypes.INSTANCE) {
            RPsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, RPsiScopedExpr.class);
            text = scope != null ? scope.getInnerText() : "";
        } else {
            RPsiUpperSymbol firstModuleIdentifier = ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
            text = ORUtil.getLongIdent(firstModuleIdentifier);
        }
        return text;
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                String name = getName();
                return name != null ? name : "";
            }

            @Override
            public @NotNull String getLocationString() {
                return "";
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return ORIcons.MODULE_TYPE;
            }
        };
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties language) {
        return getText();
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public @NotNull List<RPsiSignatureItem> getItems() {
        return emptyList();
    }
}
