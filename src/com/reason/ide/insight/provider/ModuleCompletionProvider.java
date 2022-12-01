package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import icons.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleCompletionProvider {
    private static final Log LOG = Log.create("insight.module");

    private ModuleCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("MODULE expression completion");

        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiElement dotLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = dotLeaf == null ? null : dotLeaf.getPrevSibling();

        final Collection<PsiElement> expressions = new ArrayList<>();

        if (previousElement instanceof RPsiUpperSymbol) {
            LOG.debug(" -> upper symbol", previousElement);

            PsiUpperSymbolReference reference = (PsiUpperSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            LOG.debug(" -> resolved to", resolvedElement);

            if (resolvedElement instanceof FileBase) {
                expressions.addAll(getFileModules((FileBase) resolvedElement));
            } else if (resolvedElement instanceof RPsiInnerModule) {
                expressions.addAll(getInnerModules((RPsiModule) resolvedElement));
            }
        } else {
            // empty path

            // First module to complete, use the list of files
            ModuleTopLevelIndex.processModules(project, scope, fakeModule -> {
                FileBase topFile = (FileBase) fakeModule.getContainingFile();
                if (!topFile.equals(element.getContainingFile())) {
                    expressions.add(topFile);
                }
            });

            // Add virtual namespaces
            Collection<String> namespaces = FileModuleIndexService.getService().getNamespaces(project);
            LOG.debug("  namespaces", namespaces);

            for (String namespace : namespaces) {
                resultSet.addElement(
                        LookupElementBuilder.create(namespace)
                                .withTypeText("Generated namespace")
                                .withIcon(ORIcons.VIRTUAL_NAMESPACE));
            }
        }

        if (expressions.isEmpty()) {
            LOG.trace(" -> no expressions found");
        } else {
            LOG.trace(" -> expressions", expressions);
            for (PsiElement expression : expressions) {
                if (expression instanceof FileBase) {
                    FileBase topFile = (FileBase) expression;
                    resultSet.addElement(LookupElementBuilder.
                            create(topFile.getModuleName())
                            .withTypeText(FileHelper.shortLocation(topFile))
                            .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                } else if (expression instanceof PsiNamedElement && !(expression instanceof RPsiFakeModule)) {
                    String name = ((PsiNamedElement) expression).getName();
                    resultSet.addElement(LookupElementBuilder.
                            create(name == null ? "unknown" : name)
                            .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                }
            }
        }
    }

    private static @NotNull Collection<? extends RPsiModule> getModules(@Nullable PsiElement element) {
        if (element instanceof RPsiModule) {
            return getInnerModules((RPsiModule) element);
        }
        if (element instanceof FileBase) {
            return getFileModules((FileBase) element);
        }
        return Collections.emptyList();
    }

    private static @NotNull Collection<RPsiModule> getInnerModules(@NotNull RPsiModule module) {
        List<RPsiModule> result = new ArrayList<>();

        if (module.getAlias() != null) {
            PsiElement resolvedAlias = ORUtil.resolveModuleSymbol(module.getAliasSymbol());
            result.addAll(getModules(resolvedAlias));
        } else {
            PsiElement content = ORUtil.getModuleContent(module);

            List<RPsiInclude> includes = PsiTreeUtil.getStubChildrenOfTypeAsList(content, RPsiInclude.class);
            for (RPsiInclude include : includes) {
                PsiElement includedModule = ORUtil.resolveModuleSymbol(include.getModuleReference());
                if (includedModule != null) {
                    result.addAll(getModules(includedModule));
                }
            }

            result.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(content, RPsiModule.class));
        }

        return result;
    }

    private static @NotNull List<RPsiModule> getFileModules(@NotNull FileBase element) {
        List<RPsiModule> result = new ArrayList<>();

        List<RPsiInclude> includes = PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiInclude.class);
        for (RPsiInclude include : includes) {
            PsiElement includedModule = ORUtil.resolveModuleSymbol(include.getModuleReference());
            if (includedModule != null) {
                result.addAll(getModules(includedModule));
            }
        }

        result.addAll(PsiTreeUtil.getStubChildrenOfTypeAsList(element, RPsiModule.class));

        return result;
    }
}
