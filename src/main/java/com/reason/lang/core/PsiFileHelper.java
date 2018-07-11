package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class PsiFileHelper {
    private PsiFileHelper() {
    }

    @NotNull
    public static Collection<PsiNamedElement> getExpressions(@NotNull PsiFile file) {
        Collection<PsiNamedElement> result = new ArrayList<>();

        PsiElement element = file.getFirstChild();
        while (element != null) {
            if (element instanceof PsiNamedElement) {
                result.add((PsiNamedElement) element);
            }
            element = element.getNextSibling();
        }

        return result;
    }

    @NotNull
    public static Collection<PsiType> getTypeExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiType.class);
    }

    @NotNull
    public static Collection<PsiModule> getModuleExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiModule.class);
    }

    @NotNull
    public static Collection<PsiLet> getLetExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiLet.class);
    }

    @NotNull
    public static Collection<PsiExternal> getExternalExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiExternal.class);
    }

    @NotNull
    public static Collection<PsiInclude> getIncludeExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiInclude.class);
    }

    @Nullable
    public static PsiElement getLetExpression(@NotNull PsiFile file, @NotNull String name) {
        Collection<PsiLet> letExpressions = getLetExpressions(file);
        for (PsiLet let : letExpressions) {
            if (name.equals(let.getName())) {
                return let;
            }
        }
        return null;
    }
}
