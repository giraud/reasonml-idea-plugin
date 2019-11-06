package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
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
    public static Collection<PsiNameIdentifierOwner> getExpressions(@NotNull PsiFile file) {
        ArrayList<PsiNameIdentifierOwner> result = new ArrayList<>();

        PsiFinder psiFinder = PsiFinder.getInstance(file.getProject());

        PsiElement element = file.getFirstChild();
        processSiblingExpressions(psiFinder, element, result);

        return result;
    }

    private static void processSiblingExpressions(@Nullable PsiFinder psiFinder, @Nullable PsiElement element, @NotNull List<PsiNameIdentifierOwner> result) {
        while (element != null) {
            if (element instanceof PsiInclude) {
                // Recursively include everything from referenced module
                PsiInclude include = (PsiInclude) element;
                PsiModule includedModule = psiFinder == null ? null : psiFinder.findModuleFromQn(include.getQualifiedName());
                if (includedModule != null) {
                    result.addAll(includedModule.getExpressions());
                }
            }

            if (element instanceof PsiDirective) {
                // add all elements found in a directive, can't be resolved
                processSiblingExpressions(psiFinder, element.getFirstChild(), result);
            } else if (element instanceof PsiNameIdentifierOwner) {
                result.add((PsiNameIdentifierOwner) element);
            }

            element = element.getNextSibling();
        }
    }

    @NotNull
    public static Collection<PsiNameIdentifierOwner> getExpressions(@NotNull PsiFile file, @Nullable String name) {
        Collection<PsiNameIdentifierOwner> result = new ArrayList<>();

        if (name != null) {
            PsiElement element = file.getFirstChild();
            while (element != null) {
                if (element instanceof PsiNameIdentifierOwner && name.equals(((PsiNameIdentifierOwner) element).getName())) {
                    result.add((PsiNameIdentifierOwner) element);
                }
                element = element.getNextSibling();
            }
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

    @NotNull
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

}
