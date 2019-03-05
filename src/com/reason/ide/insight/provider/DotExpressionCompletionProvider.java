package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.PsiSignatureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class DotExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final static Log LOG = Log.create("insight.dot");

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
                final List<String> qualifiedNames = m_modulePathFinder.extractPotentialPaths(cursorElement, false);

                LOG.debug("  symbol", upperName);
                LOG.debug("  potential paths", qualifiedNames);

                PsiFinder psiFinder = PsiFinder.getInstance();

                // Find file modules

                VirtualFile vFile = FileModuleIndexService.getService().getFileWithName(upperName, GlobalSearchScope.allScope(project));
                FileBase fileModule = vFile == null ? null : (FileBase) PsiManager.getInstance(project).findFile(vFile);
                LOG.debug("  file", upperName, fileModule);
                if (vFile != null) {
                    if (qualifiedNames.contains(fileModule.asModuleName())) {
                        Collection<PsiNamedElement> expressions = fileModule.getExpressions();
                        addExpressions(resultSet, expressions);
                    }
                }

                // Find modules

                Collection<PsiInnerModule> modules = psiFinder.findModules(project, upperName, interfaceOrImplementation);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("  modules", modules.size(), modules.size() == 1 ? " (" + modules.iterator().next().getName() + ")" : "");
                }

                Collection<? extends PsiQualifiedNamedElement> resolvedModules = modules.stream().
                        filter(psiModule -> qualifiedNames.contains(psiModule.getQualifiedName())).
                        map(psiModule -> {
                            PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleAlias(project, psiModule.getQualifiedName());
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
