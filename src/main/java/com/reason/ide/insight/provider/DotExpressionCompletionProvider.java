package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.lang.MlTypes;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.inBsconfig;

public class DotExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final MlTypes m_types;

    public DotExpressionCompletionProvider(MlTypes types) {
        m_types = types;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» DOT expression completion");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getPosition();
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(cursorElement);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof PsiUpperSymbol) {
            // Expression of module
            String upperName = ((PsiUpperSymbol) previousElement).getName();
            if (upperName != null) {
                Collection<PsiModule> modules = RmlPsiUtil.findModules(project, upperName, interfaceOrImplementation, inBsconfig);

                // Find the potential module paths, and filter the result
                final List<String> qualifiedNames = extractPaths(previousElement, upperName);
                Collection<PsiModule> resolvedModules = modules.stream().filter(psiModule -> qualifiedNames.contains(psiModule.getQualifiedName())).collect(Collectors.toList());

                for (PsiModule resolvedModule : resolvedModules) {
                    if (resolvedModule != null) {
                        Collection<PsiNamedElement> expressions = resolvedModule.getExpressions();
                        for (PsiNamedElement expression : expressions) {
                            resultSet.addElement(
                                    LookupElementBuilder.
                                            create(expression).
                                            withTypeText(PsiSignatureUtil.getProvidersType(expression)).
                                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                            );
                        }
                    }
                }
            }
        }
    }

    @NotNull
    private List<String> extractPaths(PsiElement previousElement, String upperName) {
        // Find the expression paths
        // Need to add implicit elements like open/include/local open/...
        String path = upperName;
        PsiElement prevSibling = previousElement.getPrevSibling();
        while (prevSibling.getNode().getElementType() == m_types.DOT) {
            prevSibling = prevSibling.getPrevSibling();
            if (prevSibling instanceof PsiUpperSymbol) {
                path = ((PsiUpperSymbol) prevSibling).getName() + "." + path;
                prevSibling = prevSibling.getPrevSibling();
            } else {
                break;
            }
        }
        final List<String> qualifiedNames = new ArrayList<>();
        qualifiedNames.add(path);
        return qualifiedNames;
    }
}
