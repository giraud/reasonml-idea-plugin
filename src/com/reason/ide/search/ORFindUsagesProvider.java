package com.reason.ide.search;

import com.intellij.lang.*;
import com.intellij.lang.findUsages.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public abstract class ORFindUsagesProvider implements FindUsagesProvider {

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof PsiModule || element instanceof PsiException ||
                element instanceof PsiLet || element instanceof PsiVal ||
                element instanceof PsiType || element instanceof PsiExternal || element instanceof RPsiRecordField ||
                element instanceof PsiVariantDeclaration || element instanceof PsiParameterDeclaration;
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
        if (element instanceof PsiModule) {
            return "Module " + ((PsiModule) element).getName();
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
