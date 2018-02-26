package com.reason.ide.insight.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.util.PsiIconUtil.getProvidersIcon;

public class JsxNameCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        //System.out.println("JsxNameCompletionProvider.addCompletions");

        RmlFile originalFile = (RmlFile) parameters.getOriginalFile();
        String fileModuleName = originalFile.asModuleName();
        Project project = originalFile.getProject();

        // Find all files that are components
        List<PsiModule> modules = RmlPsiUtil.findFileModules(project);
        for (PsiModule module : modules) {
            if (!module.getName().equals(fileModuleName) && module.isComponent()) {
                resultSet.addElement(LookupElementBuilder.create(module).
                        withIcon(getProvidersIcon(module, 0)).
                        withInsertHandler((context, item) -> {
                            char completionChar = context.getCompletionChar();
                            if (completionChar == ' ') {
                                context.setAddCompletionChar(false);
                            }

                            Editor editor = context.getEditor();
                            EditorModificationUtil.insertStringAtCaret(editor, " >");
                            editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);

                            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
                        })
                );
            }
        }
    }

    private static boolean isCharAtSpace(Editor editor) {
        final int startOffset = editor.getCaretModel().getOffset();
        final Document document = editor.getDocument();
        return document.getTextLength() > startOffset && document.getCharsSequence().charAt(startOffset) == ' ';
    }
}
