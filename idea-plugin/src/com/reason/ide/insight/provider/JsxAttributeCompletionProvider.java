package com.reason.ide.insight.provider;

import static java.util.stream.Collectors.toList;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.LayeredIcon;
import com.reason.Log;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.impl.PsiTagStartImpl;
import icons.ORIcons;
import java.util.Collection;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class JsxAttributeCompletionProvider {

  private static final Log LOG = Log.create("insight.jsxAttribute");

  private JsxAttributeCompletionProvider() {}

  public static void addCompletions(
      @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
    LOG.debug("JSX attribute completion");

    PsiTagStart tag = PsiTreeUtil.getParentOfType(element, PsiTagStart.class);
    if (tag != null) {
      List<PsiTagStart.TagProperty> attributes = tag.getUnifiedPropertyList();

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
      Collection<PsiTagProperty> usedAttributes =
          PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
      List<String> usedNames =
          usedAttributes.stream().map(PsiTagProperty::getName).collect(toList());
      LOG.debug("used names", usedNames);

      // Now populate the dialog
      for (PsiTagStart.TagProperty attribute : attributes) {
        String attributeName = attribute.getName();
        if (attributeName != null && !usedNames.contains(attributeName)) {
          boolean mandatory = attribute.isMandatory();
          Icon icon =
              mandatory
                  ? LayeredIcon.create(ORIcons.ATTRIBUTE, ORIcons.OVERLAY_MANDATORY)
                  : ORIcons.ATTRIBUTE;
          resultSet.addElement(
              LookupElementBuilder.create(attributeName)
                  .withBoldness(mandatory)
                  .withTypeText(attribute.getType(), true)
                  .withIcon(icon)
                  .withInsertHandler((context, item) -> insertTagAttributeHandler(context)));
        }
      }
    }
  }

  private static void insertTagAttributeHandler(@NotNull InsertionContext context) {
    context.setAddCompletionChar(false);

    Editor editor = context.getEditor();
    EditorModificationUtil.insertStringAtCaret(editor, "=()");
    editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 1);
  }
}
