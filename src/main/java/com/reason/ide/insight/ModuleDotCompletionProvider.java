package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleName;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

class ModuleDotCompletionProvider {
    static void complete(Project project, PsiModuleName name, @NotNull CompletionResultSet resultSet) {
        // Get the correct module
        Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, name.getName(), project, GlobalSearchScope.allScope(project), PsiModule.class);

        if (!modules.isEmpty()) {
            for (PsiModule module : modules) {
                Collection<PsiNamedElement> expressions = module.getExpressions();

                for (PsiNamedElement expression : expressions) {
                    String inferredType = expression instanceof PsiLet ? ((PsiLet) expression).getInferredType() : null;
                    resultSet.addElement(
                            LookupElementBuilder.create(expression).
                                    withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                                    withTypeText(inferredType == null ? "unknown type" : inferredType));
                }
            }
        }
    }
}
