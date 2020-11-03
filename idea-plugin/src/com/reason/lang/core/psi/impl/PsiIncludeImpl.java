package com.reason.lang.core.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.model.gotosymbol.GoToSymbolProvider;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;

public class PsiIncludeImpl extends CompositeTypePsiElement<ORTypes> implements PsiInclude {

  // region Constructors
  protected PsiIncludeImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
    super(types, elementType);
  }
  // endregion

  @Override
  public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    throw new IncorrectOperationException("Not implemented");
  }

  @NotNull
  @Override
  public String getPath() {
    PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
    if (firstChild instanceof PsiFunctorCall) {
      return ((PsiFunctorCall) firstChild).getFunctorName();
    }
    return firstChild == null ? "" : ORUtil.getTextUntilClass(firstChild, PsiConstraints.class);
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    return getPath();
  }

  @Override
  public boolean useFunctor() {
    PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
    return firstChild instanceof PsiFunctorCall;
  }

  @Override
  public ItemPresentation getPresentation() {
    return new GoToSymbolProvider.BaseNavigationItem(this, getQualifiedName(), ORIcons.INCLUDE);
  }

  @NotNull
  @Override
  public String toString() {
    return "PsiInclude " + getQualifiedName();
  }
}
