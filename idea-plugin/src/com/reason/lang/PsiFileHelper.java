package com.reason.lang;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.ExpressionFilter;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiDirective;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiFakeModule;
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
    public static Collection<PsiNamedElement> getExpressions(@Nullable PsiFile file, @NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter) {
        ArrayList<PsiNamedElement> result = new ArrayList<>();

        if (file != null) {
            PsiFinder psiFinder = PsiFinder.getInstance(file.getProject());
            QNameFinder qnameFinder = PsiFinder.getQNameFinder(file.getLanguage());
            processSiblingExpressions(psiFinder, qnameFinder, file.getFirstChild(), eScope, result, filter);
        }

        return result;
    }

    private static void processSiblingExpressions(@Nullable PsiFinder psiFinder, @NotNull QNameFinder qnameFinder, @Nullable PsiElement element,
                                                  @NotNull ExpressionScope eScope, @NotNull List<PsiNamedElement> result, @Nullable ExpressionFilter filter) {
        while (element != null) {
            if (element instanceof PsiInclude && psiFinder != null) {
                // Recursively include everything from referenced module
                PsiInclude include = (PsiInclude) element;
                GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());

                PsiModule includedModule = null;

                String includeName = include.getQualifiedName();
                for (String path : qnameFinder.extractPotentialPaths(include)) {
                    Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(path + "." + includeName, true, interfaceOrImplementation, scope);
                    if (!modulesFromQn.isEmpty()) {
                        includedModule = modulesFromQn.iterator().next();
                        break;
                    }
                }
                if (includedModule == null) {
                    Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(includeName, true, interfaceOrImplementation, scope);
                    if (!modulesFromQn.isEmpty()) {
                        includedModule = modulesFromQn.iterator().next();
                    }
                }

                if (includedModule != null) {
                    Collection<PsiNamedElement> expressions = includedModule.getExpressions(eScope, filter);
                    result.addAll(expressions);
                }
            }

            if (element instanceof PsiDirective) {
                // add all elements found in a directive, can't be resolved
                processSiblingExpressions(psiFinder, qnameFinder, element.getFirstChild(), eScope, result, filter);
            } else if (element instanceof PsiNamedElement) {
                boolean include = eScope == ExpressionScope.all || !(element instanceof PsiLet && ((PsiLet) element).isPrivate());
                if (include && (!(element instanceof PsiFakeModule)) && (filter == null || filter.accept((PsiNamedElement) element))) {
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
                if (element instanceof PsiNamedElement && name.equals(((PsiNamedElement) element).getName())) {
                    result.add((PsiNamedElement) element);
                }
                element = element.getNextSibling();
            }
        }

        return result;
    }

    @NotNull
    public static List<PsiType> getTypeExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiType.class);
    }

    @NotNull
    public static List<PsiModule> getModuleExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiInnerModule.class);
    }

    @NotNull
    public static List<PsiFunctor> getFunctorExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiFunctor.class);
    }

    @NotNull
    public static List<PsiClass> getClassExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiClass.class);
    }

    @NotNull
    public static List<PsiVal> getValExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiVal.class);
    }

    @NotNull
    public static List<PsiExternal> getExternalExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiExternal.class);
    }

    @NotNull
    public static Collection<PsiOpen> getOpenExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiOpen.class);
    }

    @NotNull
    public static List<PsiInclude> getIncludeExpressions(@Nullable PsiFile file) {
        return PsiTreeUtil.getStubChildrenOfTypeAsList(file, PsiInclude.class);
    }
}
