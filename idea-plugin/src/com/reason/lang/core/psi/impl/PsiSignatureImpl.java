package com.reason.lang.core.psi.impl;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.psi.PsiSignature;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.type.ORTypes;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureImpl extends CompositeTypePsiElement<ORTypes> implements PsiSignature {

  protected PsiSignatureImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @NotNull
  @Override
  public ORSignature asHMSignature() {
    Collection<PsiSignatureItem> items =
        PsiTreeUtil.findChildrenOfType(this, PsiSignatureItemImpl.class);
    return new ORSignature(getContainingFile().getLanguage(), items);
  }

  @NotNull
  @Override
  public String asString(@NotNull Language lang) {
    return asHMSignature().asString(lang);
  }

  @NotNull
  @Override
  public String toString() {
    return "Signature";
  }
}
