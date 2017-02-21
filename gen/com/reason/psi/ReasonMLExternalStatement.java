// This is a generated file. Not intended for manual editing.
package com.reason.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.navigation.ItemPresentation;

public interface ReasonMLExternalStatement extends PsiElement {

  @NotNull
  ReasonMLExternalDeclaration getExternalDeclaration();

  @NotNull
  ReasonMLTypeInformation getTypeInformation();

  @NotNull
  ReasonMLValueName getValueName();

  ItemPresentation getPresentation();

}
