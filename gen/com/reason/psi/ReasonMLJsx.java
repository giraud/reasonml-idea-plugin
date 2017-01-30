// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface ReasonMLJsx extends PsiElement {

  @Nullable
  ReasonMLEndTag getEndTag();

  @NotNull
  List<ReasonMLJsxContent> getJsxContentList();

  @NotNull
  ReasonMLStartTag getStartTag();

  @NotNull
  List<ReasonMLTagProperty> getTagPropertyList();

}
