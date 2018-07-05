package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PsiFileHelper {
    private PsiFileHelper() {
    }

    @NotNull
    public static Collection<PsiType> getTypeExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiType.class);
    }

    @NotNull
    public static Collection<PsiModule> getModuleExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiModule.class);
    }

    @NotNull
    public static Collection<PsiLet> getLetExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiLet.class);
    }

    @NotNull
    public static Collection<PsiNamedElement> getExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.findChildrenOfAnyType(file, PsiType.class, PsiModule.class, PsiLet.class, PsiExternal.class, PsiVal.class);
    }

    @NotNull
    public static Collection<PsiExternal> getExternalExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiExternal.class);
    }

    @NotNull
    public static Collection<PsiInclude> getIncludeExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.findChildrenOfType(file, PsiInclude.class);
    }

    @Nullable
    public static PsiElement getLetExpression(@NotNull PsiFile file, @NotNull String name) {
        return getLetExpressions(file).stream().filter(let -> name.equals(let.getName())).findFirst().orElseGet(null);
    }
}
