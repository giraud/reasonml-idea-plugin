package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiSignatureImpl extends CompositeTypePsiElement<ORTypes> implements PsiSignature {

  PsiSignatureImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  public String getName() {
    return getText();  // TODO name extraction
  }

  @Override
  public @NotNull ORSignature asHMSignature() {
    Collection<PsiSignatureItem> items = PsiTreeUtil.findChildrenOfType(this, PsiSignatureItemImpl.class);
    return new ORSignature(getLanguage(), items);
  }

  @Override
  public @NotNull String asString(@NotNull Language lang) {
    return asHMSignature().asString(lang);
  }

  @Override
  public @NotNull String toString() {
    return "Signature";
  }
}
