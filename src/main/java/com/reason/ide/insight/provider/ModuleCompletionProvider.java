package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.search.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class ModuleCompletionProvider {
    private static final Log LOG = Log.create("insight.module");

    private ModuleCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull GlobalSearchScope scope, @NotNull CompletionResultSet resultSet) {
        LOG.debug("MODULE expression completion");

        Project project = element.getProject();
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof RPsiUpperSymbol) {
            LOG.debug(" -> upper symbol", previousElement);

            ORPsiUpperSymbolReference reference = (ORPsiUpperSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            LOG.debug(" -> resolved to", resolvedElement);

            if (resolvedElement instanceof RPsiModule) {
                for (RPsiInnerModule module : PsiTreeUtil.getStubChildrenOfTypeAsList(((RPsiModule) resolvedElement).getBody(), RPsiInnerModule.class)) {
                    addModule(module, resultSet);
                }

                // Find alternatives
                ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(resolvedElement.getContainingFile());
                for (String alternateName : data.getValues(resolvedElement)) {
                    for (PsiElement alternateModule : ORReferenceAnalyzer.resolvePath(alternateName, project, scope, 0)) {
                        if (alternateModule instanceof RPsiModule) {
                            for (RPsiInnerModule module : PsiTreeUtil.getStubChildrenOfTypeAsList(((RPsiModule) alternateModule).getBody(), RPsiInnerModule.class)) {
                                addModule(module, resultSet);
                            }
                        }
                    }
                }
            }
        } else {
            // Empty path

            // First module to complete, use the list of files
            PsiFile containingFile = element.getContainingFile();
            if (containingFile instanceof RPsiModule) {
                String topModuleName = ((RPsiModule) containingFile).getModuleName();
                for (FileModuleData moduleData : FileModuleIndexService.getService().getTopModules(project, scope)) {
                    if (!moduleData.getModuleName().equals(topModuleName) && !moduleData.hasNamespace()) {
                        resultSet.addElement(LookupElementBuilder.
                                create(moduleData.getModuleName())
                                .withTypeText(moduleData.getFullName())
                                .withIcon(IconProvider.getDataModuleIcon(moduleData)));
                    }
                }
            }

            // Add virtual namespaces
            for (String namespace : FileModuleIndexService.getService().getNamespaces(project, scope)) {
                resultSet.addElement(
                        LookupElementBuilder.create(namespace)
                                .withTypeText("Generated namespace")
                                .withIcon(ORIcons.VIRTUAL_NAMESPACE));
            }
        }
    }

    private static void addModule(@NotNull RPsiModule module, @NotNull CompletionResultSet resultSet) {
        String name = module.getModuleName();
        resultSet.addElement(LookupElementBuilder.
                create(name == null ? "unknown" : name)
                .withIcon(PsiIconUtil.getProvidersIcon(module, 0)));
    }
}
