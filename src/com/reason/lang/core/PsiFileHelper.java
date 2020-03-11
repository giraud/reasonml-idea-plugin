package com.reason.lang.core;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiDirective;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class PsiFileHelper {

    private PsiFileHelper() {
    }

    @NotNull
    public static Collection<PsiNameIdentifierOwner> getExpressions(@NotNull PsiFile file, @NotNull ExpressionScope eScope) {
        ArrayList<PsiNameIdentifierOwner> result = new ArrayList<>();

        PsiFinder psiFinder = PsiFinder.getInstance(file.getProject());

        PsiElement element = file.getFirstChild();
        processSiblingExpressions(psiFinder, element, eScope, result);

        return result;
    }

    private static void processSiblingExpressions(@Nullable PsiFinder psiFinder, @Nullable PsiElement element, @NotNull ExpressionScope eScope,
                                                  @NotNull List<PsiNameIdentifierOwner> result) {
        while (element != null) {
            if (element instanceof PsiInclude && psiFinder != null) {
                // Recursively include everything from referenced module
                PsiInclude include = (PsiInclude) element;
                GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());
                Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(include.getQualifiedName(), interfaceOrImplementation, scope);
                for (PsiModule includedModule : modulesFromQn) {
                    result.addAll(includedModule.getExpressions(eScope));
                }
            }

            if (element instanceof PsiDirective) {
                // add all elements found in a directive, can't be resolved
                processSiblingExpressions(psiFinder, element.getFirstChild(), eScope, result);
            } else if (element instanceof PsiNameIdentifierOwner) {
                boolean include = !(element instanceof PsiLet && ((PsiLet) element).isPrivate());
                if (include) {
                    result.add((PsiNameIdentifierOwner) element);
                }
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
