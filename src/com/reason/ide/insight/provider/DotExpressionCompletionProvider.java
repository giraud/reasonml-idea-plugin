package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PsiIconUtil;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.PsiSignatureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class DotExpressionCompletionProvider {

    private final static Log LOG = Log.create("insight.dot");

    private DotExpressionCompletionProvider() {
    }

    public static void addCompletions(@NotNull ModulePathFinder modulePathFinder, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("DOT expression completion");

        Project project = element.getProject();
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof PsiUpperSymbol) {
            // Expression of module
            String upperName = ((PsiUpperSymbol) previousElement).getName();
            if (upperName != null) {
                // Find potential module paths, and filter the result
                final List<String> qualifiedNames = modulePathFinder.extractPotentialPaths(element, false);

                LOG.debug("  symbol", upperName);
                LOG.debug("  potential paths", qualifiedNames);

                PsiFinder psiFinder = PsiFinder.getInstance(project);

                // Find modules

                Collection<PsiModule> modules = psiFinder.findModules(upperName, interfaceOrImplementation, GlobalSearchScope.allScope(project));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("  modules", modules.size(), modules.size() == 1 ? " (" + modules.iterator().next().getName() + ")" : "");
                }

                Collection<? extends PsiQualifiedNamedElement> resolvedModules = modules.stream().
                        filter(psiModule -> qualifiedNames.contains(psiModule.getQualifiedName())).
                        map(psiModule -> {
                            PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleAlias(psiModule.getQualifiedName());
                            return moduleAlias == null ? psiModule : moduleAlias;
                        }).
                        collect(Collectors.toList());
                LOG.debug("  resolved", resolvedModules);

                for (PsiQualifiedNamedElement resolvedModule : resolvedModules) {
                    if (resolvedModule != null) {
                        Collection<PsiNamedElement> expressions = resolvedModule instanceof FileBase ? ((FileBase) resolvedModule).getExpressions() : ((PsiInnerModule) resolvedModule).getExpressions();
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
                PsiFinder psiFinder = PsiFinder.getInstance(project);

                // try let
                Collection<PsiLet> lets = psiFinder.findLets(lowerName, interfaceOrImplementation);
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

    private static void addExpressions(@NotNull CompletionResultSet resultSet, Collection<PsiNamedElement> expressions) {
        for (PsiNamedElement expression : expressions) {
            if (!(expression instanceof PsiOpen) && !(expression instanceof PsiInclude) && !(expression instanceof PsiAnnotation)) {
                // TODO: if include => include
                String name = expression.getName();
                if (name != null) {
                    String signature = PsiSignatureUtil.getSignature(expression);
                    resultSet.addElement(LookupElementBuilder.
                            create(name).
                            withTypeText(signature).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                    );
                }
                if (expression instanceof PsiType) {
                    PsiType eType = (PsiType) expression;
                    Collection<PsiVariantConstructor> variants = eType.getVariants();
                    if (!variants.isEmpty()) {
                        for (PsiVariantConstructor variant : variants) {
                            String variantName = variant.getName();
                            if (variantName != null) {
                                resultSet.addElement(LookupElementBuilder.
                                        create(variantName).
                                        withTypeText(eType.getName()).
                                        withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
                            }
                        }
                    }
                }
            }
        }
    }

}
