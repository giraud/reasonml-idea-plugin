package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.ui.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import icons.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.stream.Collectors.*;

public class JsxAttributeCompletionProvider {
    private static final Log LOG = Log.create("insight.jsx.attribute");

    private JsxAttributeCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX attribute completion");

        PsiTagStart tag = PsiTreeUtil.getParentOfType(element, PsiTagStart.class);
        if (tag != null) {
            List<ComponentPropertyAdapter> attributes = tag.getUnifiedPropertyList();

            if (tag.getNameIdentifier() instanceof PsiUpperSymbol) {
                // Additional attributes for UpperSymbol => only key and ref
                attributes.add(PsiTagStartImpl.createProp("key", "string=?"));
                attributes.add(PsiTagStartImpl.createProp("ref", "Js.nullable(Dom.element) => unit=?"));
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Tag found", tag.getName());
                LOG.debug("attributes", attributes);
            }

            // Attributes already used
            Collection<PsiTagProperty> usedAttributes = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
            List<String> usedNames = usedAttributes.stream().map(PsiTagProperty::getName).collect(toList());
            LOG.debug("used names", usedNames);

            // Now populate the dialog
            for (ComponentPropertyAdapter attribute : attributes) {
                String attributeName = attribute.getName();
                if (attributeName != null && !usedNames.contains(attributeName)) {
                    boolean mandatory = attribute.isMandatory();
                    Icon icon = mandatory ? LayeredIcon.create(ORIcons.ATTRIBUTE, ORIcons.OVERLAY_MANDATORY) : ORIcons.ATTRIBUTE;
                    LookupElementBuilder lookupElementBuilder = attribute.getElement() == null ? LookupElementBuilder.create(attributeName) : LookupElementBuilder.createWithSmartPointer(attributeName, attribute.getElement());
                    resultSet.addElement(PrioritizedLookupElement.withPriority(
                            lookupElementBuilder
                                    .withBoldness(mandatory)
                                    .withTypeText(attribute.getType(), true)
                                    .withIcon(icon)
                                    .withInsertHandler((context, item) -> insertTagAttributeHandler(context)),
                            mandatory ? 1 : 0));
                }
            }
        }
    }

    private static void insertTagAttributeHandler(@NotNull InsertionContext context) {
        context.setAddCompletionChar(false);

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, "={}");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
    }
}
