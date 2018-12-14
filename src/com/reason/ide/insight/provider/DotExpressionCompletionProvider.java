package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class DotExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    @NotNull
    private final static Log LOG = new Log("insight.dot");

    private final ModulePathFinder m_modulePathFinder;

    public DotExpressionCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        LOG.debug("DOT expression completion");

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

                LOG.debug("  symbol", upperName);
                LOG.debug("  potential paths", qualifiedNames);

                PsiFinder psiFinder = PsiFinder.getInstance();

                // Find file modules

                FileBase fileModule = psiFinder.findFileModule(project, upperName);
                LOG.debug("  file", (PsiFile) fileModule);
                if (fileModule != null) {
                    if (qualifiedNames.contains(fileModule.asModuleName())) {
                        Collection<PsiNamedElement> expressions = fileModule.getExpressions();
                        addExpressions(resultSet, expressions);
                    }
                }

                // Find modules

                Collection<PsiModule> modules = psiFinder.findModules(project, upperName, interfaceOrImplementation);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("  modules", modules.size(), modules.size() == 1 ? " (" + modules.iterator().next().getName() + ")" : "");
                }

                Collection<? extends PsiQualifiedNamedElement> resolvedModules = modules.stream().
                        map(psiModule -> {
                            PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleAlias(project, psiModule.getQualifiedName());
                            return moduleAlias == null ? psiModule : moduleAlias;
                        }).
                        filter(psiModule -> qualifiedNames.contains(psiModule.getQualifiedName())).
                        collect(Collectors.toList());
                LOG.debug("  resolved", resolvedModules);

                for (PsiQualifiedNamedElement resolvedModule : resolvedModules) {
                    if (resolvedModule != null) {
                        Collection<PsiNamedElement> expressions = resolvedModule instanceof FileBase ? ((FileBase) resolvedModule).getExpressions() : ((PsiModule) resolvedModule).getExpressions();
                        LOG.debug("  expressions", expressions);
                        addExpressions(resultSet, expressions);
                    }
                }
            }
        } else if (previousElement instanceof PsiLowerSymbol) {
            // Expression of let/val/external/type
            String lowerName = ((PsiLowerSymbol) previousElement).getName();
            if (lowerName != null) {
                LOG.debug("  symbol", lowerName);
                PsiFinder psiFinder = PsiFinder.getInstance();

                // try let
                Collection<PsiLet> lets = psiFinder.findLets(project, lowerName, interfaceOrImplementation);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("  lets", lets.size(), lets.size() == 1 ? " (" + lets.iterator().next().getName() + ")" : "[" + Joiner.join(", ", lets) + "]");
                }

                // need filtering

                for (PsiLet expression : lets) {
                    for (PsiRecordField recordField : expression.getObjectFields()) {
                        resultSet.addElement(
                                LookupElementBuilder.
                                        create(recordField).
                                        withTypeText(PsiSignatureUtil.getSignature(recordField)).
                                        withIcon(PsiIconUtil.getProvidersIcon(recordField, 0))
                        );
                    }
                }
            }
        }
    }

    private void addExpressions(@NotNull CompletionResultSet resultSet, Collection<PsiNamedElement> expressions) {
        for (PsiNamedElement expression : expressions) {
            if (!(expression instanceof PsiOpen) && !(expression instanceof PsiInclude) && !(expression instanceof PsiAnnotation)) {
                // TODO: if include => include
                resultSet.addElement(LookupElementBuilder.
                        create(expression).
                        withTypeText(PsiSignatureUtil.getSignature(expression)).
                        withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                );

                if (expression instanceof PsiType) {
                    PsiType eType = (PsiType) expression;
                    Collection<PsiVariantConstructor> variants = eType.getVariants();
                    if (!variants.isEmpty()) {
                        for (PsiVariantConstructor variant : variants) {
                            resultSet.addElement(LookupElementBuilder.
                                    create(variant).
                                    withTypeText(eType.getName()).
                                    withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
                        }
                    }
                }
            }
        }
    }

}
