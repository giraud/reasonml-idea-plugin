package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Joiner;
import com.reason.ide.Debug;
import com.reason.ide.files.FileBase;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;

public class DotExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final ModulePathFinder m_modulePathFinder;
    private final Debug m_debug;

    public DotExpressionCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.dot"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("DOT expression completion");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getPosition();
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(cursorElement);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof PsiUpperSymbol) {
            // Expression of module
            String upperName = ((PsiUpperSymbol) previousElement).getName();
            if (upperName != null) {
                // Find potential module paths, and filter the result
                final List<String> qualifiedNames = m_modulePathFinder.extractPotentialPaths(cursorElement);

                m_debug.debug("  symbol", upperName);
                m_debug.debug("  potential paths", qualifiedNames);

                PsiFinder psiFinder = PsiFinder.getInstance();

                // Find file modules

                FileBase fileModule = psiFinder.findFileModule(project, upperName);
                m_debug.debug("  file", fileModule);
                if (fileModule != null) {
                    if (qualifiedNames.contains(fileModule.asModuleName())) {
                        Collection<PsiNamedElement> expressions = fileModule.getExpressions();
                        for (PsiNamedElement expression : expressions) {
                            resultSet.addElement(LookupElementBuilder.
                                    create(expression).
                                    withTypeText(PsiSignatureUtil.getProvidersType(expression)).
                                    withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                            );
                        }

                    }
                }

                // Find modules

                Collection<PsiModule> modules = psiFinder.findModules(project, upperName, interfaceOrImplementation);
                if (m_debug.isDebugEnabled()) {
                    m_debug.debug("  modules", modules.size(), modules.size() == 1 ? " (" + modules.iterator().next().getName() + ")" : "");
                }

                Collection<? extends PsiQualifiedNamedElement> resolvedModules = modules.stream().
                        map(psiModule -> {
                            PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleAlias(project, psiModule.getQualifiedName());
                            return moduleAlias == null ? psiModule : moduleAlias;
                        }).
                        filter(psiModule -> qualifiedNames.contains(psiModule.getQualifiedName())).
                        collect(Collectors.toList());
                if (m_debug.isDebugEnabled()) {
                    m_debug.debug("  resolved", resolvedModules);
                }

                for (PsiQualifiedNamedElement resolvedModule : resolvedModules) {
                    if (resolvedModule != null) {
                        Collection<PsiNamedElement> expressions = resolvedModule instanceof FileBase ? ((FileBase) resolvedModule).getExpressions() : ((PsiModule) resolvedModule).getExpressions();
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
        } else if (previousElement instanceof PsiLowerSymbol) {
            // Expression of let/val/external/type
            String lowerName = ((PsiLowerSymbol) previousElement).getName();
            if (lowerName != null) {
                m_debug.debug("  symbol", lowerName);
                PsiFinder psiFinder = PsiFinder.getInstance();

                // try let
                Collection<PsiLet> lets = psiFinder.findLets(project, lowerName, interfaceOrImplementation);
                if (m_debug.isDebugEnabled()) {
                    m_debug.debug("  lets", lets.size(), lets.size() == 1 ? " (" + lets.iterator().next().getName() + ")" : "[" + Joiner.join(", ", lets) + "]");
                }

                // need filtering

                for (PsiLet expression : lets) {
                    for (PsiRecordField recordField : expression.getObjectFields()) {
                        resultSet.addElement(
                                LookupElementBuilder.
                                        create(recordField).
                                        withTypeText(PsiSignatureUtil.getProvidersType(recordField)).
                                        withIcon(PsiIconUtil.getProvidersIcon(recordField, 0))
                        );
                    }
                }
            }
        }
    }

}
