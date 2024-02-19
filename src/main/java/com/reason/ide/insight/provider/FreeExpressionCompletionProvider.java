package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.intellij.util.indexing.*;
import com.reason.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.ide.ORFileUtils.*;

public class FreeExpressionCompletionProvider {
    private static final Log LOG = Log.create("insight.free");

    private FreeExpressionCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull GlobalSearchScope searchScope, @NotNull CompletionResultSet resultSet) {
        LOG.debug("FREE expression completion");

        Project project = element.getProject();
        PsiFile containingFile = element.getContainingFile();
        String topModuleName = containingFile instanceof FileBase ? ((FileBase) containingFile).getModuleName() : null;
        ORLanguageProperties languageProperties = ORLanguageProperties.cast(element.getLanguage());
        FileModuleIndexService fileModuleIndexService = FileModuleIndexService.getService();

        // Add virtual namespaces
        for (String namespace : fileModuleIndexService.getNamespaces(project, searchScope)) {
            resultSet.addElement(
                    LookupElementBuilder.create(namespace)
                            .withTypeText("Generated namespace")
                            .withIcon(ORIcons.VIRTUAL_NAMESPACE));
        }

        // Add file modules (that are not a component & not namespaced)
        for (FileModuleData topModuleData : fileModuleIndexService.getTopModules(project, searchScope)) {
            if (!topModuleData.getModuleName().equals(topModuleName) && !topModuleData.isComponent() && !topModuleData.hasNamespace()) {
                resultSet.addElement(
                        LookupElementBuilder.create(topModuleData.getModuleName())
                                .withTypeText(topModuleData.getFullName())
                                .withIcon(IconProvider.getDataModuleIcon(topModuleData)));
            }
        }

        // Add expressions from opened dependencies in config
        VirtualFile virtualFile = getVirtualFile(containingFile);
        BsConfig config = project.getService(ORCompilerConfigManager.class).getNearestConfig(virtualFile);
        if (config != null) {
            for (String dependency : config.getOpenedDeps()) {
                RPsiModule topModule = getTopModule(dependency, project, searchScope);
                if (topModule != null) {
                    addModuleExpressions(topModule, languageProperties, searchScope, resultSet);
                }
            }
        }

        // Pervasives is always included
        RPsiModule pervasives = getTopModule("Pervasives", project, searchScope);
        if (pervasives != null) {
            addModuleExpressions(pervasives, languageProperties, searchScope, resultSet);
        }

        // Add all local expressions
        PsiElement item = element.getPrevSibling();
        if (item == null) {
            item = element.getParent();
        }

        boolean skipLet = false;

        while (item != null) {
            if (item instanceof RPsiLetBinding) {
                skipLet = true;
            } else if (item instanceof RPsiInnerModule
                    || item instanceof RPsiLet
                    || item instanceof RPsiType
                    || item instanceof RPsiExternal
                    || item instanceof RPsiException
                    || item instanceof RPsiVal) {
                RPsiLet letItem = item instanceof RPsiLet ? (RPsiLet) item : null;
                if (letItem != null && skipLet) {
                    skipLet = false;
                } else if (letItem != null && letItem.isDeconstruction()) {
                    for (PsiElement deconstructedElement : letItem.getDeconstructedElements()) {
                        resultSet.addElement(
                                LookupElementBuilder.create(deconstructedElement.getText())
                                        .withTypeText(RPsiSignatureUtil.getSignature(item, ORLanguageProperties.cast(element.getLanguage())))
                                        .withIcon(ORIcons.LET));
                    }
                } else if (letItem == null || !letItem.isAnonymous()) {
                    PsiNamedElement expression = (PsiNamedElement) item;
                    resultSet.addElement(
                            LookupElementBuilder.create(expression)
                                    .withTypeText(RPsiSignatureUtil.getSignature(expression, ORLanguageProperties.cast(element.getLanguage())))
                                    .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                    if (item instanceof RPsiType) {
                        expandType((RPsiType) item, resultSet);
                    }
                }
            } else if (item instanceof RPsiOpen openItem) {
                RPsiUpperSymbol moduleSymbol = ORUtil.findImmediateLastChildOfClass(openItem, RPsiUpperSymbol.class);
                ORPsiUpperSymbolReference reference = moduleSymbol != null ? moduleSymbol.getReference() : null;
                PsiElement resolved = reference != null ? reference.resolveInterface() : null;
                if (resolved instanceof RPsiModule resolvedModule) {
                    addModuleExpressions(resolvedModule, languageProperties, searchScope, resultSet);
                }
            } else if (item instanceof RPsiInclude includeItem) {
                RPsiUpperSymbol moduleSymbol = ORUtil.findImmediateLastChildOfClass(includeItem, RPsiUpperSymbol.class);
                ORPsiUpperSymbolReference reference = moduleSymbol != null ? moduleSymbol.getReference() : null;
                PsiElement resolved = reference != null ? reference.resolveInterface() : null;
                if (resolved instanceof RPsiModule resolvedModule) {
                    addModuleExpressions(resolvedModule, languageProperties, searchScope, resultSet);
                }
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                item = parent instanceof RPsiInnerModule ? parent.getPrevSibling() : parent;
            } else {
                item = prevItem;
            }
        }
    }

    private static @Nullable RPsiModule getTopModule(@NotNull String name, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        FileModuleIndex index = FileModuleIndex.getInstance();
        ID<String, FileModuleData> indexId = index != null ? index.getName() : null;
        if (indexId != null) {
            PsiManager psiManager = PsiManager.getInstance(project);
            Collection<VirtualFile> containingFiles = FileBasedIndex.getInstance().getContainingFiles(indexId, name, scope);
            VirtualFile virtualFile = containingFiles.stream().min((o1, o2) -> FileHelper.isInterface(o1.getFileType()) ? -1 : FileHelper.isInterface(o2.getFileType()) ? 1 : 0).orElse(null);
            if (virtualFile != null) {
                PsiFile psiFile = psiManager.findFile(virtualFile);
                return psiFile instanceof RPsiModule ? (RPsiModule) psiFile : null;
            }
        }

        return null;
    }

    private static void expandType(@NotNull RPsiType type, @NotNull CompletionResultSet resultSet) {
        Collection<RPsiVariantDeclaration> variants = type.getVariants();
        if (!variants.isEmpty()) {
            for (RPsiVariantDeclaration variant : variants) {
                resultSet.addElement(
                        LookupElementBuilder.create(variant)
                                .withTypeText(type.getName())
                                .withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
            }
        }
    }

    private static void addModuleExpressions(@NotNull RPsiModule rootModule, @Nullable ORLanguageProperties language, @NotNull GlobalSearchScope searchScope, @NotNull CompletionResultSet resultSet) {
        // alternate names
        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(rootModule.getContainingFile());
        for (String alternateName : data.getValues(rootModule)) {
            // Try to resolve as an inner module or a top module
            Collection<RPsiModule> alternateModules = ModuleFqnIndex.getElements(alternateName, rootModule.getProject(), searchScope);
            RPsiModule topModule = getTopModule(alternateName, rootModule.getProject(), searchScope);
            if (topModule != null) {
                alternateModules.add(topModule);
            }

            for (RPsiModule alternateModule : alternateModules) {
                addModuleExpressions(alternateModule, language, searchScope, resultSet);
            }
        }

        for (PsiNamedElement item : ORUtil.findImmediateChildrenOfClass(rootModule.getBody(), PsiNamedElement.class)) {
            if (!(item instanceof RPsiLet) || !(((RPsiLet) item).isPrivate() || ((RPsiLet) item).isAnonymous())) {
                if (item instanceof RPsiLet && ((RPsiLet) item).isDeconstruction()) {
                    for (PsiElement deconstructedElement : ((RPsiLet) item).getDeconstructedElements()) {
                        resultSet.addElement(
                                LookupElementBuilder.create(deconstructedElement.getText())
                                        .withTypeText(RPsiSignatureUtil.getSignature(item, language))
                                        .withIcon(ORIcons.LET));
                    }
                } else if (!(item instanceof RPsiAnnotation)) {
                    String itemName = item.getName();
                    if (itemName != null && !itemName.isEmpty() && !itemName.equals("unknown")) {
                        resultSet.addElement(
                                LookupElementBuilder.create(item)
                                        .withTypeText(RPsiSignatureUtil.getSignature(item, language))
                                        .withIcon(PsiIconUtil.getProvidersIcon(item, 0))
                                        .withInsertHandler(FreeExpressionCompletionProvider::insertExpression));
                    }
                }
            }
        }
    }

    private static void insertExpression(@NotNull InsertionContext insertionContext, @NotNull LookupElement element) {
        PsiElement psiElement = element.getPsiElement();
        if (psiElement instanceof RPsiLet let) {
            if (let.isFunction()) {
                insertionContext.setAddCompletionChar(false);
                Editor editor = insertionContext.getEditor();
                EditorModificationUtil.insertStringAtCaret(editor, "()");
                editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
            }
        }
    }
}
