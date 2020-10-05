package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PsiRecord extends ASTWrapperPsiElement {

  public PsiRecord(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean canNavigate() {
    return false;
  }

  @NotNull
  public List<PsiRecordField> getFields() {
    PsiElement[] children = getChildren();
    if (children.length == 0) {
      return ContainerUtil.emptyList();
    }

    List<PsiRecordField> result = new ArrayList<>(children.length);
    for (PsiElement child : children) {
      if (child instanceof PsiRecordField) {
        result.add((PsiRecordField) child);
      }
    }

    return result;
  }

  @NotNull
  @Override
  public String toString() {
    return "Record";
  }
}
