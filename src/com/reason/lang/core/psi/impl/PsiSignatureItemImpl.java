package com.reason.lang.core.psi.impl;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLanguageConverter;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureItemImpl extends CompositeTypePsiElement<ORTypes>
    implements PsiSignatureItem {

  protected PsiSignatureItemImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }

  @Override
  public boolean isNamedItem() {
    PsiElement firstChild = getFirstChild();
    return firstChild != null && ORUtil.nextSiblingWithTokenType(firstChild, m_types.COLON) != null;
  }

  @NotNull
  @Override
  public String asText(@NotNull Language language) {
    PsiElement firstChild = getFirstChild();
    if (firstChild instanceof PsiLanguageConverter) {
      return ((PsiLanguageConverter) firstChild).asText(language);
    }
    return getText();
  }

  @NotNull
  @Override
  public String toString() {
    return "Signature item";
  }
}
