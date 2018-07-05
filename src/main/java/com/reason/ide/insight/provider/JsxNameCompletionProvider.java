package com.reason.ide.insight.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.reason.ide.Debug;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.util.PsiIconUtil.getProvidersIcon;
import static com.reason.lang.core.MlFileType.interfaceOrImplementation;

public class JsxNameCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final Debug m_debug;

    public JsxNameCompletionProvider() {
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.jsxname"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("JSX name expression completion");

        RmlFile originalFile = (RmlFile) parameters.getOriginalFile();
        String fileModuleName = originalFile.asModuleName();
        Project project = originalFile.getProject();

        // Find all files that are components ! TODO: components can be sub modules
        Collection<PsiFile> files = PsiFinder.getInstance().findFileModules(project, interfaceOrImplementation);
        m_debug.debug("Files found", files.size());
        for (PsiFile file : files) {
            String moduleName = ((FileBase) file).asModuleName();
            if (m_debug.isDebugEnabled()) {
                m_debug.debug(" is component", moduleName, ((FileBase) file).isComponent());
            }
            if (!fileModuleName.equals(moduleName) && ((FileBase) file).isComponent()) {
                resultSet.addElement(LookupElementBuilder.create(file).
                        withIcon(getProvidersIcon(file, 0)).
                        withTypeText(PsiSignatureUtil.getProvidersType(PsiFileHelper.getLetExpression(file, "make"))).
                        withInsertHandler((context, item) -> insertTagNameHandler(project, context, moduleName))
                );
            }
        }
    }

    private static void insertTagNameHandler(Project project, InsertionContext context, String tagName) {
        char completionChar = context.getCompletionChar();
        if (completionChar == ' ') {
            context.setAddCompletionChar(false);
        }

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, " ></" + tagName + ">");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 4 - tagName.length());

        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
    }
}
