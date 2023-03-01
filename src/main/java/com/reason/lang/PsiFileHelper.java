package com.reason.lang;

import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.reason.ide.search.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.lang.core.ORFileType.*;

public class PsiFileHelper {

    private PsiFileHelper() {
    }

    @NotNull
    public static Collection<PsiNamedElement> getExpressions(@Nullable PsiFile file, @NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter) {
        ArrayList<PsiNamedElement> result = new ArrayList<>();

        if (file != null) {
            PsiFinder psiFinder = file.getProject().getService(PsiFinder.class);
            QNameFinder qnameFinder = QNameFinderFactory.getQNameFinder(file.getLanguage());
            processSiblingExpressions(psiFinder, qnameFinder, file.getFirstChild(), eScope, result, filter);
        }

        return result;
    }

    private static void processSiblingExpressions(@Nullable PsiFinder psiFinder, @NotNull QNameFinder qnameFinder, @Nullable PsiElement element, @NotNull ExpressionScope eScope, @NotNull List<PsiNamedElement> result, @Nullable ExpressionFilter filter) {
        while (element != null) {
            if (element instanceof RPsiInclude && psiFinder != null) {
                // Recursively include everything from referenced module
                RPsiInclude include = (RPsiInclude) element;
                GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());

                RPsiModule includedModule = null;

                String includedPath = include.getIncludePath();
                for (String path : qnameFinder.extractPotentialPaths(include)) {
                    Set<RPsiModule> modulesFromQn = psiFinder.findModulesFromQn(path + "." + includedPath, true, interfaceOrImplementation);
                    if (!modulesFromQn.isEmpty()) {
                        includedModule = modulesFromQn.iterator().next();
                        break;
                    }
                }
                if (includedModule == null) {
                    Set<RPsiModule> modulesFromQn = psiFinder.findModulesFromQn(includedPath, true, interfaceOrImplementation);
                    if (!modulesFromQn.isEmpty()) {
                        includedModule = modulesFromQn.iterator().next();
                    }
                }

                if (includedModule != null) {
                    Collection<PsiNamedElement> expressions = includedModule.getExpressions(eScope, filter);
                    result.addAll(expressions);
                }
            }

            if (element instanceof RPsiDirective) {
                // add all elements found in a directive, can't be resolved
                processSiblingExpressions(
                        psiFinder, qnameFinder, element.getFirstChild(), eScope, result, filter);
            } else if (element instanceof PsiNamedElement) {
                boolean include =
                        eScope == ExpressionScope.all
                                || !(element instanceof RPsiLet && ((RPsiLet) element).isPrivate());
                if (include
                        && (!(element instanceof RPsiFakeModule))
                        && (filter == null || filter.accept((PsiNamedElement) element))) {
                    result.add((PsiNamedElement) element);
                }
            }

            element = element.getNextSibling();
        }
    }

    @NotNull
    public static Collection<PsiNamedElement> getExpressions(@NotNull PsiFile file, @Nullable String name) {
        Collection<PsiNamedElement> result = new ArrayList<>();

        if (name != null) {
            PsiElement element = file.getFirstChild();
            while (element != null) {
                if (element instanceof PsiNamedElement
                        && name.equals(((PsiNamedElement) element).getName())) {
                    result.add((PsiNamedElement) element);
                }
                element = element.getNextSibling();
            }
        }

        return result;
    }

    @NotNull
    public static List<RPsiModule> getModuleExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiInnerModule.class);
    }

    @NotNull
    public static List<RPsiFunctor> getFunctorExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiFunctor.class);
    }

    @NotNull
    public static List<RPsiClass> getClassExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiClass.class);
    }

    @NotNull
    public static List<RPsiVal> getValExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiVal.class);
    }

    @NotNull
    public static List<RPsiExternal> getExternalExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiExternal.class);
    }

    @NotNull
    public static Collection<RPsiOpen> getOpenExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiOpen.class);
    }

    @NotNull
    public static List<RPsiInclude> getIncludeExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiInclude.class);
    }
}
