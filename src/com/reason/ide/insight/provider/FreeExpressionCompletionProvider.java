package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.Collections.emptyList;

public class FreeExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Log LOG = new Log("insight.free");
    private final ModulePathFinder m_modulePathFinder;

    public FreeExpressionCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        LOG.debug("FREE expression completion");

        PsiFinder psiFinder = PsiFinder.getInstance();
        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getOriginalPosition();

        List<String> paths = cursorElement == null ? emptyList() : m_modulePathFinder.extractPotentialPaths(cursorElement);
        LOG.debug("potential paths", paths);

        // Add paths (opens and local opens for ex)
        for (String path : paths) {
            PsiQualifiedNamedElement module = psiFinder.findModuleFromQn(project, path);
            if (module != null) {
                Collection<PsiNamedElement> expressions = (module instanceof FileBase) ? ((FileBase) module).getExpressions() : ((PsiModule) module).getExpressions();
                for (PsiNamedElement expression : expressions) {
                    if (!(expression instanceof PsiAnnotation)) {
                        resultSet.addElement(LookupElementBuilder.
                                create(expression).
                                withTypeText(PsiSignatureUtil.getSignature(expression)).
                                withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                                withInsertHandler(this::insertExpression));
                    }
                }
            }
        }

        // Add all local expressions (let and module name)
        PsiElement item = cursorElement == null ? null : cursorElement.getPrevSibling();
        if (item == null && cursorElement != null) {
            item = cursorElement.getParent();
        }
        while (item != null) {
            if (item instanceof PsiModule || item instanceof PsiLet || item instanceof PsiType || item instanceof PsiExternal || item instanceof PsiException || item instanceof PsiVal) {
                PsiNamedElement element = (PsiNamedElement) item;
                resultSet.addElement(LookupElementBuilder.
                        create(element).
                        withTypeText(PsiSignatureUtil.getSignature(element)).
                        withIcon(PsiIconUtil.getProvidersIcon(element, 0)));
                if (item instanceof PsiType) {
                    expandType((PsiType) item, resultSet);
                }
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                item = parent instanceof PsiModule ? parent.getPrevSibling() : parent;
            } else {
                item = prevItem;
            }
        }

        // Add pervasives expressions
        PsiModule pervasives = psiFinder.findModule(project, "Pervasives", interfaceOrImplementation);
        if (pervasives != null) {
            for (PsiNamedElement expression : pervasives.getExpressions()) {
                resultSet.addElement(LookupElementBuilder.create(expression).
                        withTypeText(PsiSignatureUtil.getSignature(expression)).
                        withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
            }
        }

        // Add file modules
        Collection<FileBase> files = psiFinder.findFileModules(project, interfaceOrImplementation);
        for (FileBase file : files) {
            if (!file.isComponent()) {
                resultSet.addElement(LookupElementBuilder.
                        create(file.asModuleName()).
                        withTypeText(file.shortLocation(project)).
                        withIcon(PsiIconUtil.getProvidersIcon(file, 0)));
            } else {
                LOG.debug("Component found, skip", (PsiFile) file);
            }
        }
    }

    private void expandType(@NotNull PsiType type, @NotNull CompletionResultSet resultSet) {
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

    private void insertExpression(@NotNull InsertionContext insertionContext, LookupElement element) {
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
