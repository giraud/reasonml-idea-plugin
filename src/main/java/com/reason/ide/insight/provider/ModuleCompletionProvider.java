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
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import static com.intellij.openapi.application.ApplicationManager.*;

public class ModuleCompletionProvider {
    private static final Log LOG = Log.create("insight.module");

    private ModuleCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull GlobalSearchScope scope, @NotNull CompletionResultSet resultSet) {
        LOG.debug("MODULE expression completion");

        Project project = element.getProject();
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        if (previousElement instanceof RPsiUpperSymbol previousUpperSymbol) {
            LOG.debug(" -> upper symbol", previousUpperSymbol);

            PsiElement resolvedElement = previousUpperSymbol.getReference().resolve();
            LOG.debug(" -> resolved to", resolvedElement);

            if (resolvedElement instanceof RPsiModule resolvedModule) {
                for (RPsiInnerModule module : PsiTreeUtil.getStubChildrenOfTypeAsList(resolvedModule.getBody(), RPsiInnerModule.class)) {
                    addModule(module, resultSet);
                }

                // Find alternatives
                ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(resolvedModule.getContainingFile());
                for (String alternateName : data.getValues(resolvedModule)) {
                    for (PsiElement alternateElement : ORReferenceAnalyzer.resolvePath(alternateName, project, scope, 0)) {
                        if (alternateElement instanceof RPsiModule alternateModule) {
                            for (RPsiInnerModule module : PsiTreeUtil.getStubChildrenOfTypeAsList(alternateModule.getBody(), RPsiInnerModule.class)) {
                                addModule(module, resultSet);
                            }
                        }
                    }
                }
            }
        } else {
            // Empty path
            FileModuleIndexService fileModuleIndexService = getApplication().getService(FileModuleIndexService.class);

            // First module to complete, use the list of files
            PsiFile containingFile = element.getContainingFile();
            if (containingFile instanceof RPsiModule) {
                String topModuleName = ((RPsiModule) containingFile).getModuleName();
                for (FileModuleData moduleData : fileModuleIndexService.getTopModules(project, scope)) {
                    if (!moduleData.getModuleName().equals(topModuleName) && !moduleData.hasNamespace()) {
                        resultSet.addElement(LookupElementBuilder.
                                create(moduleData.getModuleName())
                                .withTypeText(moduleData.getFullName())
                                .withIcon(IconProvider.getDataModuleIcon(moduleData)));
                    }
                }
            }

            // Add virtual namespaces
            for (String namespace : fileModuleIndexService.getNamespaces(project, scope)) {
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
