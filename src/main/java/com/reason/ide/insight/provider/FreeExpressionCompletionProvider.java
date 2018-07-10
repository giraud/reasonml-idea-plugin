package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.Debug;
import com.reason.ide.files.FileBase;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;

public class FreeExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Debug m_debug;
    private final ModulePathFinder m_modulePathFinder;

    public FreeExpressionCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.free"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("FREE expression completion");

        PsiFinder psiFinder = PsiFinder.getInstance();
        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getOriginalPosition();

        List<String> paths = m_modulePathFinder.extractPotentialPaths(cursorElement);
        m_debug.debug("potential paths", paths);

        // Add paths (opens and local opens for ex)
        for (String path : paths) {
            PsiQualifiedNamedElement module = psiFinder.findModuleFromQn(project, path);
            if (module != null) {
                Collection<PsiNamedElement> expressions = (module instanceof FileBase) ? ((FileBase) module).getExpressions() : ((PsiModule) module).getExpressions();
                for (PsiNamedElement expression : expressions) {
                    resultSet.addElement(LookupElementBuilder.
                            create(expression).
                            withTypeText(PsiSignatureUtil.getProvidersType(expression)).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                            withInsertHandler(this::insertExpression));
                }
            }
        }

        // Add all local expressions (let and module name)
        PsiElement item = cursorElement.getPrevSibling();
        if (item == null) {
            item = cursorElement.getParent();
        }
        while (item != null) {
            if (item instanceof PsiModule || item instanceof PsiLet || item instanceof PsiType || item instanceof PsiExternal || item instanceof PsiException || item instanceof PsiVal) {
                PsiNamedElement element = (PsiNamedElement) item;
                resultSet.addElement(LookupElementBuilder.create(element).
                        withTypeText(PsiSignatureUtil.getProvidersType(element)).
                        withIcon(PsiIconUtil.getProvidersIcon(element, 0)));
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
                        withTypeText(PsiSignatureUtil.getProvidersType(expression)).
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
            }
        }
    }

    private void insertExpression(InsertionContext insertionContext, LookupElement element) {
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
