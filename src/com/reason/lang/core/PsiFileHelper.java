package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PsiFileHelper {
    private PsiFileHelper() {
    }

    @NotNull
    public static Collection<PsiNamedElement> getExpressions(@NotNull PsiFile file) {
        ArrayList<PsiNamedElement> result = new ArrayList<>();

        PsiFinder psiFinder = PsiFinder.getInstance(file.getProject());

        PsiElement element = file.getFirstChild();
        while (element != null) {
            if (element instanceof PsiInclude) {
                // Recursively include everything from referenced module
                PsiInclude include = (PsiInclude) element;
                PsiModule includedModule = psiFinder.findModuleFromQn(include.getQualifiedName());
                if (includedModule != null) {
                    result.addAll(includedModule.getExpressions());
                }
            }
            if (element instanceof PsiNamedElement) {
                result.add((PsiNamedElement) element);
            }
            element = element.getNextSibling();
        }

        return result;
    }

    @NotNull
    public static Collection<PsiNamedElement> getExpressions(@NotNull PsiFile file, @NotNull String name) {
        Collection<PsiNamedElement> result = new ArrayList<>();

        PsiElement element = file.getFirstChild();
        while (element != null) {
            if (element instanceof PsiNamedElement && name.equals(((PsiNamedElement) element).getName())) {
                result.add((PsiNamedElement) element);
            }
            element = element.getNextSibling();
        }

        return result;
    }

    @NotNull
    public static List<PsiType> getTypeExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiType.class);
    }

    @NotNull
    public static List<PsiInnerModule> getModuleExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiInnerModule.class);
    }

    @NotNull
    public static List<PsiFunctor> getFunctorExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiFunctor.class);
    }

    @NotNull
    public static List<PsiClass> getClassExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiClass.class);
    }

    @NotNull
    public static List<PsiLet> getLetExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiLet.class);
    }

    public static List<PsiVal> getValExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiVal.class);
    }

    @NotNull
    public static List<PsiExternal> getExternalExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiExternal.class);
    }

    @NotNull
    public static Collection<PsiOpen> getOpenExpressions(PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiOpen.class);
    }

    @NotNull
    public static List<PsiInclude> getIncludeExpressions(@NotNull PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiInclude.class);
    }

    @Nullable
    public static PsiElement getLetExpression(@NotNull PsiFile file, @NotNull String name) {
        List<PsiLet> letExpressions = getLetExpressions(file);
        for (PsiLet let : letExpressions) {
            if (name.equals(let.getName())) {
                return let;
            }
        }
        return null;
    }

    @Nullable
    public static PsiInnerModule getModuleExpression(@NotNull PsiFile file, @NotNull String name) {
        Collection<PsiInnerModule> modules = getModuleExpressions(file);
        for (PsiInnerModule module : modules) {
            if (name.equals(module.getName())) {
                return module;
            }
        }
        return null;
    }
}
