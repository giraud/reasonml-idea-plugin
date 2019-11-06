package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PsiIconUtil;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.IconProvider;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.IndexedFileModule;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.PsiSignatureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.reason.lang.QNameFinder.includeAll;
import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class DotExpressionCompletionProvider {

    private final static Log LOG = Log.create("insight.dot");

    private DotExpressionCompletionProvider() {
    }

    public static void addCompletions(@NotNull QNameFinder qnameFinder, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("DOT expression completion");

        Project project = element.getProject();
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof PsiUpperSymbol) {
            String upperName = ((PsiUpperSymbol) previousElement).getName();
            if (upperName != null) {
                PsiFinder psiFinder = PsiFinder.getInstance(project);
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);

                // Find potential module paths, and filter the result
                final Set<String> qualifiedNames = qnameFinder.extractPotentialPaths(element, includeAll, false).
                        stream().
                        map(qname -> {
                            PsiModule moduleFromQn = psiFinder.findModuleFromQn(qname);
                            return moduleFromQn == null ? qname : moduleFromQn.getQualifiedName();
                        }).
                        collect(Collectors.toSet());

                LOG.debug("  symbol", upperName);
                LOG.debug("  potential paths (no aliases)", qualifiedNames);

                // Might be a virtual namespace

                Collection<IndexedFileModule> modulesForNamespace = psiFinder.findModulesForNamespace(upperName, scope);
                if (!modulesForNamespace.isEmpty()) {
                    LOG.debug("  found namespace files", modulesForNamespace);

                    for (IndexedFileModule file : modulesForNamespace) {
                        resultSet.addElement(LookupElementBuilder.
                                create(file.getModuleName()).
                                withTypeText(FileHelper.shortLocation(project, file.getPath())).
                                withIcon(IconProvider.getFileModuleIcon(file.isOCaml(), file.isInterface())));
                    }

                    return;
                }

                // Find modules

                Collection<PsiModule> modules = psiFinder.findModules(upperName, interfaceOrImplementation, scope);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("  modules", modules.size(), modules.size() == 1 ? " (" + modules.iterator().next().getQualifiedName() + ")" : "");
                }

                Collection<? extends PsiModule> resolvedModules = modules.stream().
                        map(psiModule -> {
                            String namespace = psiFinder.findNamespace(psiModule, scope);
                            String moduleQname = namespace.isEmpty() ? psiModule.getQualifiedName() : namespace + "." + psiModule.getQualifiedName();
                            PsiModule moduleAlias = psiFinder.findModuleAlias(moduleQname);
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(moduleQname + " (alias=" + moduleAlias + ") " + psiModule.getContainingFile().getVirtualFile().getPath());
                            }
                            String name = moduleAlias == null ? moduleQname : moduleAlias.getQualifiedName();

                            if (qualifiedNames.contains(name)) {
                                return moduleAlias == null ? psiModule : moduleAlias;
                            }

                            return null;
                        }).
                        filter(Objects::nonNull).
                        collect(Collectors.toList());
                LOG.debug("  resolved", resolvedModules);

                for (PsiModule resolvedModule : resolvedModules) {
                    if (resolvedModule != null) {
                        Collection<PsiNameIdentifierOwner> expressions = resolvedModule.getExpressions();
                        LOG.debug("  expressions", expressions);
                        addExpressions(resultSet, expressions, element.getLanguage());
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
                    for (PsiRecordField recordField : expression.getRecordFields()) {
                        resultSet.addElement(
                                LookupElementBuilder.
                                        create(recordField).
                                        withTypeText(PsiSignatureUtil.getSignature(recordField, element.getLanguage())).
                                        withIcon(PsiIconUtil.getProvidersIcon(recordField, 0))
                        );
                    }
                }
            }
        }
    }

    private static void addExpressions(@NotNull CompletionResultSet resultSet, @NotNull Collection<PsiNameIdentifierOwner> expressions, @NotNull Language language) {
        for (PsiNameIdentifierOwner expression : expressions) {
            if (!(expression instanceof PsiOpen) && !(expression instanceof PsiInclude) && !(expression instanceof PsiAnnotation)) {
                // TODO: if include => include
                String name = expression.getName();
                if (name != null) {
                    String signature = PsiSignatureUtil.getSignature(expression, language);
                    resultSet.addElement(LookupElementBuilder.
                            create(name).
                            withTypeText(signature).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                    );
                }
                if (expression instanceof PsiType) {
                    PsiType eType = (PsiType) expression;
                    Collection<PsiVariantDeclaration> variants = eType.getVariants();
                    if (!variants.isEmpty()) {
                        for (PsiVariantDeclaration variant : variants) {
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
