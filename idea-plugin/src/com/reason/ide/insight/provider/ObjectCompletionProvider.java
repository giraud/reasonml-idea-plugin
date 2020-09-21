package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PsiIconUtil;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiObjectField;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class ObjectCompletionProvider {

  private static final Log LOG = Log.create("insight.object");

  private ObjectCompletionProvider() {}

  public static void addCompletions(
      @NotNull ORTypes types, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
    LOG.debug("OBJECT expression completion");

    Project project = element.getProject();
    List<String> path = new ArrayList<>();
    PsiElement previousLeaf = PsiTreeUtil.prevVisibleLeaf(element);
    if (previousLeaf != null) {
      IElementType previousElementType = previousLeaf.getNode().getElementType();

      while (previousLeaf != null && previousElementType == types.LIDENT
          || previousElementType == types.SHARPSHARP
          || previousElementType == types.SHARP) {
        if (previousElementType == types.LIDENT) {
          LeafPsiElement node = (LeafPsiElement) previousLeaf.getNode();
          path.add(node.getParent().getText());
        }
        previousLeaf = PsiTreeUtil.prevLeaf(previousLeaf);
        previousElementType = previousLeaf == null ? null : previousLeaf.getNode().getElementType();
      }
      Collections.reverse(path);
    }

    if (path.isEmpty() || path.get(0) == null) {
      return;
    }

    String lowerName = path.remove(0);

    PsiLet let = null;
    Set<PsiLet> lets =
        PsiFinder.getInstance(project).findLets(lowerName, interfaceOrImplementation);
    if (!lets.isEmpty()) {
      let = lets.iterator().next();
    }

    if (let == null) {
      return;
    }

    if (let.isRecord()) {
      Collection<PsiRecordField> fields = let.getRecordFields();
      for (PsiRecordField field : fields) {
        String name = field.getName();
        if (name != null) {
          resultSet.addElement(
              LookupElementBuilder.create(name).withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
        }
      }
    } else if (let.isJsObject()) {
      Collection<PsiObjectField> fields = let.getJsObjectFieldsForPath(path);
      for (PsiObjectField field : fields) {
        String name = field.getName();
        if (name != null) {
          resultSet.addElement(
              LookupElementBuilder.create(name).withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
        }
      }
    }
  }
}
