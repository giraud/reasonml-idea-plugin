package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.PsiIconUtil;
import com.reason.Icons;
import com.reason.Log;
import com.reason.ide.IconProvider;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.IndexedFileModule;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.PsiSignatureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FreeExpressionCompletionProvider {

    private static final Log LOG = Log.create("insight.free");

    public static void addCompletions(@NotNull QNameFinder qnameFinder, @NotNull String containingFilePath, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("FREE expression completion");

        Project project = element.getProject();
        FileModuleIndexService orFinder = FileModuleIndexService.getService();

        // Add virtual namespaces
        Collection<String> namespaces = orFinder.getNamespaces(project);
        LOG.debug("  namespaces", namespaces);
        for (String namespace : namespaces) {
            resultSet.addElement(LookupElementBuilder.
                    create(namespace).
                    withTypeText("Generated namespace").
                    withIcon(Icons.VIRTUAL_NAMESPACE));
        }

        // Add file modules (that are not a component and without namespaces)
        Collection<IndexedFileModule> filesWithoutNamespace = orFinder.getFilesWithoutNamespace(project).
                stream().
                filter(indexedFileModule -> !containingFilePath.equals(indexedFileModule.getPath()) && !indexedFileModule.getModuleName().equals("Pervasives")).
                collect(Collectors.toList());
        if (LOG.isDebugEnabled()) {
            LOG.debug("  files without namespaces", filesWithoutNamespace);
        }

        for (IndexedFileModule file : filesWithoutNamespace) {
            resultSet.addElement(LookupElementBuilder.
                    create(file.getModuleName()).
                    withTypeText(FileHelper.shortLocation(project, file.getPath())).
                    withIcon(IconProvider.getFileModuleIcon(file.isOCaml(), file.isInterface())));
        }

        PsiFinder psiFinder = PsiFinder.getInstance(project);
        Set<String> paths = qnameFinder.extractPotentialPaths(element, EnumSet.noneOf(QNameFinder.Includes.class), false);
        LOG.debug("potential paths", paths);

        // Add paths (opens and local opens for ex)
        for (String path : paths) {
            PsiModule module = psiFinder.findModuleFromQn(path);
            if (module != null) {
                if (module instanceof FileBase) {
                    if (module.equals(element.getContainingFile())) {
                        // if the module is already the containing file, we do nothing,
                        // local expressions will be added after
                        continue;
                    }
                }

                Collection<PsiNameIdentifierOwner> expressions = module.getExpressions();
                for (PsiNamedElement expression : expressions) {
                    if (!(expression instanceof PsiAnnotation)) {
                        resultSet.addElement(LookupElementBuilder.
                                create(expression).
                                withTypeText(PsiSignatureUtil.getSignature(expression, element.getLanguage())).
                                withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                                withInsertHandler(FreeExpressionCompletionProvider::insertExpression));
                    }
                }
            }
        }

        // Add all local expressions
        PsiElement item = element.getPrevSibling();
        if (item == null) {
            item = element.getParent();
        }

        while (item != null) {
            if (item instanceof PsiInclude) {
                PsiModule moduleFromQn = psiFinder.findModuleFromQn(((PsiInclude) item).getQualifiedName());
                if (moduleFromQn != null) {
                    for (PsiNamedElement expression : moduleFromQn.getExpressions()) {
                        resultSet.addElement(LookupElementBuilder.
                                create(expression).
                                withTypeText(PsiSignatureUtil.getSignature(expression, element.getLanguage())).
                                withIcon(PsiIconUtil.getProvidersIcon(element, 0)));
                        if (item instanceof PsiType) {
                            expandType((PsiType) item, resultSet);
                        }
                    }
                }
            } else if (item instanceof PsiInnerModule || item instanceof PsiLet || item instanceof PsiType || item instanceof PsiExternal || item instanceof PsiException || item instanceof PsiVal) {
                PsiNamedElement expression = (PsiNamedElement) item;
                resultSet.addElement(LookupElementBuilder.
                        create(expression).
                        withTypeText(PsiSignatureUtil.getSignature(expression, element.getLanguage())).
                        withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                if (item instanceof PsiType) {
                    expandType((PsiType) item, resultSet);
                }
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                item = parent instanceof PsiInnerModule ? parent.getPrevSibling() : parent;
            } else {
                item = prevItem;
            }
        }
    }

    private static void expandType(@NotNull PsiType type, @NotNull CompletionResultSet resultSet) {
        Collection<PsiVariantDeclaration> variants = type.getVariants();
        if (!variants.isEmpty()) {
            for (PsiVariantDeclaration variant : variants) {
                resultSet.addElement(LookupElementBuilder.
                        create(variant).
                        withTypeText(type.getName()).
                        withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
            }
        }
    }

    private static void insertExpression(@NotNull InsertionContext insertionContext, LookupElement element) {
        PsiElement psiElement = element.getPsiElement();
        if (psiElement instanceof PsiLet) {
            PsiLet let = (PsiLet) psiElement;
            if (let.isFunction()) {
                insertionContext.setAddCompletionChar(false);
                Editor editor = insertionContext.getEditor();
                EditorModificationUtil.insertStringAtCaret(editor, "()");
                editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
            }
        }
    }
}
