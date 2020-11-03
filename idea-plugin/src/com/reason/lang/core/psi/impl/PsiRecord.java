package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.reason.lang.core.psi.PsiRecordField;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PsiRecord extends CompositePsiElement {

  protected PsiRecord(IElementType type) {
    super(type);
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
