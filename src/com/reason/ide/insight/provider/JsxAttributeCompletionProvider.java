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
import com.intellij.ui.LayeredIcon;
import com.intellij.util.ProcessingContext;
import com.reason.icons.Icons;
import com.reason.ide.Debug;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.impl.PsiTagStartImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JsxAttributeCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final ModulePathFinder m_modulePathFinder;
    private final Debug m_debug;

    public JsxAttributeCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.jsxAttribute"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("JSX attribute completion");

        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiTagStart tag = PsiTreeUtil.getParentOfType(originalPosition, PsiTagStart.class);
        if (tag != null) {
            List<PsiTagStart.TagProperty> attributes = tag.getUnifiedPropertyList();

            if (tag.getNameIdentifier() instanceof PsiUpperSymbol) {
                // Additional attributes for UpperSymbol => only key and ref
                attributes.add(PsiTagStartImpl.createProp("key", "string=?"));
                attributes.add(PsiTagStartImpl.createProp("ref", "Js.nullable(Dom.element) => unit=?"));
            }

            if (m_debug.isDebugEnabled()) {
                m_debug.debug("Tag found", tag.getName());
                m_debug.debug("attributes", attributes);
            }

            // Attributes already used
            Collection<PsiTagProperty> usedAttributes = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
            List<String> usedNames = usedAttributes.stream().map(PsiTagProperty::getName).collect(toList());
            m_debug.debug("used names", usedNames);

            // Now populate the dialog
            for (PsiTagStart.TagProperty attribute : attributes) {
                if (!usedNames.contains(attribute.getName())) {
                    boolean mandatory = attribute.isMandatory();
                    Icon icon = mandatory ? LayeredIcon.create(Icons.ATTRIBUTE, Icons.OVERLAY_MANDATORY) : Icons.ATTRIBUTE;
                    resultSet.addElement(LookupElementBuilder.
                            create(attribute.getName()).
                            withBoldness(mandatory).
                            withTypeText(attribute.getType(), true).
                            withIcon(icon).
                            withInsertHandler((context, item) -> insertTagAttributeHandler(context)));
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
