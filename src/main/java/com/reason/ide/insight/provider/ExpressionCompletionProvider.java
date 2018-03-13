package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {
    public ExpressionCompletionProvider(MlTypes types) {

    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        System.out.println("»» Expression completion");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getOriginalPosition();
        PsiElement previousElement = cursorElement == null ? null : cursorElement.getPrevSibling();
        previousElement = previousElement == null ? null : previousElement.getPrevSibling();

        // Find the expression path

        if (previousElement instanceof PsiUpperSymbol) {
            // Expression of module
            String upperName = ((PsiUpperSymbol) previousElement).getName();
            if (upperName != null) {
                PsiModule module = null;

                Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, upperName, project, GlobalSearchScope.allScope(project), PsiModule.class);
                if (!modules.isEmpty()) {
                    // TODO: Find the correct module path
                    module = modules.iterator().next();
                }

                if (module != null) {
                    Collection<PsiNamedElement> expressions = module.getExpressions();
                    for (PsiNamedElement expression : expressions) {
                        resultSet.addElement(
                                LookupElementBuilder.
                                        create(expression).
                                        withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                        );
                    }
                }
            }
        }
    }
}
