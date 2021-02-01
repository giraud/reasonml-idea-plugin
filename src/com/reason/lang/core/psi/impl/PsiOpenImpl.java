package com.reason.lang.core.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.model.gotosymbol.GoToSymbolProvider;
import com.reason.lang.core.CompositeTypePsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiOpenImpl extends CompositeTypePsiElement<ORTypes> implements PsiOpen {

  // region Constructors
  protected PsiOpenImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
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
    // Skip `let` and `open`
    PsiElement firstChild = getFirstChild();
    if (firstChild != null && firstChild.getNode().getElementType() == m_types.LET) { // `let open` in OCaml
      firstChild = ORUtil.nextSibling(firstChild);
    }
    // Skip force open
    PsiElement child = PsiTreeUtil.skipWhitespacesForward(firstChild);
    if (child != null && child.getNode().getElementType() == m_types.EXCLAMATION_MARK) {
      child = PsiTreeUtil.skipWhitespacesForward(child);
    }

    if (child instanceof PsiFunctorCall) {
      return ((PsiFunctorCall) child).getFunctorName();
    }
    return child == null ? "" : ORUtil.getTextUntilTokenType(child, null);
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
    return new GoToSymbolProvider.BaseNavigationItem(this, getQualifiedName(), ORIcons.OPEN);
  }

  @Nullable
  @Override
  public String toString() {
    return "Open " + getQualifiedName();
  }
}
