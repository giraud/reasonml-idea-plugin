package com.reason.lang.core.psi;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

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
}
