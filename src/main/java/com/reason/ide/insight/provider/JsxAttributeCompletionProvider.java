package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.reason.icons.Icons;
import com.reason.ide.Debug;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class JsxAttributeCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final ModulePathFinder m_modulePathFinder;
    private final Debug m_debug;

    public JsxAttributeCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.jsxattribute"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("JSX expression completion");

        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiTagStart tag = PsiTreeUtil.getParentOfType(originalPosition, PsiTagStart.class);
        if (tag != null) {
            Map<String, String> attributes = tag.getAttributes();

            // TODO: additional attributes for UpperSymbol => only key and ref
            //attributes.put("key", "string=?");
            //attributes.put("ref", "Js.nullable(Dom.element) => unit=?");

            // Attributes already used
            Collection<PsiTagProperty> usedAttributes = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
            List<String> usedNames = usedAttributes.stream().map(PsiTagProperty::getName).collect(toList());

            // Now populate the dialog
            for (Map.Entry<String, String> attributeEntry : attributes.entrySet()) {
                String attributeName = attributeEntry.getKey();
                if (!usedNames.contains(attributeName)) {
                    resultSet.addElement(LookupElementBuilder.create(attributeName).
                            withTypeText(attributeEntry.getValue(), true).
                            withIcon(Icons.ATTRIBUTE).
                            withInsertHandler((context, item) -> insertTagAttributeHandler(context))
                    );
                }
            }
        }
    }

    private static void insertTagAttributeHandler(InsertionContext context) {
        context.setAddCompletionChar(false);

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, "=()");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
    }
}
