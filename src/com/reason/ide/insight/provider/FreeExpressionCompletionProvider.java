package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.impl.PsiAnnotation;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import icons.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.lang.core.ExpressionFilterConstants.*;
import static com.reason.lang.core.psi.impl.ExpressionScope.*;

public class FreeExpressionCompletionProvider {
    private static final Log LOG = Log.create("insight.free");

    private FreeExpressionCompletionProvider() {
    }

    public static void addCompletions(@NotNull QNameFinder qnameFinder, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("FREE expression completion");

        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        FileBase containingFile = (FileBase) element.getContainingFile();

        // Add virtual namespaces
        Collection<String> namespaces = FileModuleIndexService.getService().getNamespaces(project);
        LOG.debug("  namespaces", namespaces);

        for (String namespace : namespaces) {
            resultSet.addElement(
                    LookupElementBuilder.create(namespace)
                            .withTypeText("Generated namespace")
                            .withIcon(ORIcons.VIRTUAL_NAMESPACE));
        }

        // Add file modules (that are not a component and without namespaces)
        PsiManager psiManager = PsiManager.getInstance(project);
        ModuleTopLevelIndex.processModules(project, scope, topModule -> {
            FileBase topModuleFile = (FileBase) topModule.getContainingFile();
            if (!topModuleFile.equals(containingFile)) {
                VirtualFile virtualFile = topModuleFile.getVirtualFile();
                PsiFile psiFile = psiManager.findFile(virtualFile);
                resultSet.addElement(
                        LookupElementBuilder.create(topModule.getModuleName())
                                .withTypeText(psiFile == null ? virtualFile.getName() : FileHelper.shortLocation(psiFile))
                                .withIcon(IconProvider.getFileModuleIcon(topModuleFile)));
            }
        });

        Set<String> paths = qnameFinder.extractPotentialPaths(element);
        paths.add("Pervasives");
        LOG.debug("potential paths", paths);

        // Add paths (opens and local opens for example)
        for (String path : paths) {
            Collection<PsiModule> modulesFromQn = ModuleFqnIndex.getElements(path, project, scope);
            for (PsiModule module : modulesFromQn) {
                if (module.getContainingFile().equals(containingFile)) {
                    // if the module is already the containing file, we do nothing,
                    // local expressions will be added after
                    continue;
                }

                Collection<PsiNamedElement> expressions = module.getExpressions(pub, NO_FILTER);
                for (PsiNamedElement expression : expressions) {
                    if (!(expression instanceof PsiAnnotation)) {
                        resultSet.addElement(
                                LookupElementBuilder.create(expression)
                                        .withTypeText(PsiSignatureUtil.getSignature(expression, ORLanguageProperties.cast(element.getLanguage())))
                                        .withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                                        .withInsertHandler(FreeExpressionCompletionProvider::insertExpression));
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
            if (item instanceof PsiInnerModule
                    || item instanceof PsiLet
                    || item instanceof PsiType
                    || item instanceof PsiExternal
                    || item instanceof PsiException
                    || item instanceof PsiVal) {
                if (item instanceof PsiLet && ((PsiLet) item).isDeconstruction()) {
                    for (PsiElement deconstructedElement : ((PsiLet) item).getDeconstructedElements()) {
                        resultSet.addElement(
                                LookupElementBuilder.create(deconstructedElement.getText())
                                        .withTypeText(PsiSignatureUtil.getSignature(item, ORLanguageProperties.cast(element.getLanguage())))
                                        .withIcon(ORIcons.LET));
                    }
                } else {
                    PsiNamedElement expression = (PsiNamedElement) item;
                    resultSet.addElement(
                            LookupElementBuilder.create(expression)
                                    .withTypeText(PsiSignatureUtil.getSignature(expression, ORLanguageProperties.cast(element.getLanguage())))
                                    .withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                    if (item instanceof PsiType) {
                        expandType((PsiType) item, resultSet);
                    }
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
                resultSet.addElement(
                        LookupElementBuilder.create(variant)
                                .withTypeText(type.getName())
                                .withIcon(PsiIconUtil.getProvidersIcon(variant, 0)));
            }
        }
    }

    private static void insertExpression(
            @NotNull InsertionContext insertionContext, @NotNull LookupElement element) {
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
