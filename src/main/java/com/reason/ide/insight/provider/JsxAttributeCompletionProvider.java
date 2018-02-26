package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.reason.icons.Icons;
import com.reason.lang.core.psi.PsiTagStart;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JsxAttributeCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» JSX attribute completion");

        Project project = parameters.getOriginalFile().getProject();

        PsiElement parent = parameters.getOriginalPosition().getParent();
        if (parent instanceof PsiTagStart) {
            PsiTagStart tag = (PsiTagStart) parent;
            Map<String, String> attributes = tag.getAttributes();
            for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {
                resultSet.addElement(LookupElementBuilder.create(attributeEntry.getKey()).
                        withTypeText(attributeEntry.getValue(), true).
                        withIcon(Icons.ATTRIBUTE).
                        withInsertHandler((context, item) -> insertTagAttributeHandler(project, context))
                );
            }
        }
    }

    private static void insertTagAttributeHandler(Project project, InsertionContext context) {
        context.setAddCompletionChar(false);

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, "=()");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
    }
}
