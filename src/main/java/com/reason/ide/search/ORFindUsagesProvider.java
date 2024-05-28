package com.reason.ide.search;

import com.intellij.lang.*;
import com.intellij.lang.findUsages.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public abstract class ORFindUsagesProvider implements FindUsagesProvider {
    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof RPsiModule/*Functor*/ || element instanceof RPsiException ||
                element instanceof RPsiLet || element instanceof RPsiVal ||
                element instanceof RPsiType || element instanceof RPsiExternal ||
                element instanceof RPsiRecordField || element instanceof RPsiObjectField ||
                element instanceof RPsiVariantDeclaration || element instanceof RPsiParameterDeclaration ||
                (element instanceof RPsiLowerSymbol && element.getParent() instanceof RPsiDeconstruction);
    }

    @Override
    public @Nullable String getHelpId(@NotNull PsiElement psiElement) {
        return HelpID.FIND_OTHER_USAGES;
    }

    @Override
    public @NotNull String getType(@NotNull PsiElement element) {
        String type = PsiTypeElementProvider.getType(element);
        return type == null ? "unknown type" : type;
    }

    @Override
    public @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof RPsiModule) {
            return "Module " + ((RPsiModule) element).getName();
        } else if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            return name == null ? "" : name;
        }

        return "";
    }

    @Override
    public @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof PsiQualifiedNamedElement) {
            String qName = ((PsiQualifiedNamedElement) element).getQualifiedName();
            return qName == null ? "" : qName;
        }
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            if (name != null) {
                return name;
            }
        }

        return element.getText();
    }
}
