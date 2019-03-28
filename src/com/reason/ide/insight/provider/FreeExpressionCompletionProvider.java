package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.PsiIconUtil;
import com.reason.Icons;
import com.reason.Log;
import com.reason.ide.IconProvider;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.IndexedFileModule;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.PsiSignatureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class FreeExpressionCompletionProvider {

    private static final Log LOG = Log.create("insight.free");

    public static void addCompletions(@NotNull ModulePathFinder m_modulePathFinder, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("FREE expression completion");

        Project project = element.getProject();

        FileModuleIndexService orFinder = FileModuleIndexService.getService();
        PsiManager psiManager = PsiManager.getInstance(project);

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
        Collection<IndexedFileModule> filesWithoutNamespace = orFinder.getFilesWithoutNamespace(project);
        if (LOG.isDebugEnabled()) {
            LOG.debug("  files without namespaces", filesWithoutNamespace.size());
        }
        for (IndexedFileModule file : filesWithoutNamespace) {
            resultSet.addElement(LookupElementBuilder.
                    create(file.getModuleName()).
                    withTypeText(FileHelper.shortLocation(project, file.getPath())).
                    withIcon(IconProvider.getFileModuleIcon(file.isOCaml(), file.isInterface())));
        }

        PsiFinder psiFinder = PsiFinder.getInstance(project);
        List<String> paths = m_modulePathFinder.extractPotentialPaths(element, false);
        LOG.debug("potential paths", paths);

        // Add paths (opens and local opens for ex)
        for (String path : paths) {
            PsiQualifiedNamedElement module = psiFinder.findModuleFromQn(path);
            if (module != null) {
                Collection<PsiNamedElement> expressions = (module instanceof FileBase) ? ((FileBase) module).getExpressions() : ((PsiInnerModule) module).getExpressions();
                for (PsiNamedElement expression : expressions) {
                    if (!(expression instanceof PsiAnnotation)) {
                        resultSet.addElement(LookupElementBuilder.
                                create(expression).
                                withTypeText(PsiSignatureUtil.getSignature(expression)).
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
                                withTypeText(PsiSignatureUtil.getSignature(expression)).
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
                        withTypeText(PsiSignatureUtil.getSignature(expression)).
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

        // Add pervasives expressions
        Collection<VirtualFile> pervasivesFile = orFinder.getInterfaceFilesWithName("Pervasives", GlobalSearchScope.allScope(project));
        if (!pervasivesFile.isEmpty()) {
            PsiFile file = psiManager.findFile(pervasivesFile.iterator().next());
            if (file instanceof FileBase) {
                FileBase pervasives = pervasivesFile.isEmpty() ? null : (FileBase) file;
                if (pervasives != null) {
                    for (PsiNamedElement expression : pervasives.getExpressions()) {
                        if (!(expression instanceof PsiAnnotation)) {
                            resultSet.addElement(LookupElementBuilder.
                                    create(expression).
                                    withTypeText(PsiSignatureUtil.getSignature(expression)).
                                    withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                            if (expression instanceof PsiType) {
                                expandType((PsiType) expression, resultSet);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void expandType(@NotNull PsiType type, @NotNull CompletionResultSet resultSet) {
        Collection<PsiVariantConstructor> variants = type.getVariants();
        if (!variants.isEmpty()) {
            for (PsiVariantConstructor variant : variants) {
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
