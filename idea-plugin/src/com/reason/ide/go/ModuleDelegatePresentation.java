package com.reason.ide.go;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiQualifiedElement;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

class ModuleDelegatePresentation extends ORDelegatePsiElement
    implements NavigationItem, PsiQualifiedElement {
  private final @NotNull ItemPresentation m_presentation;

  public ModuleDelegatePresentation(
      @NotNull PsiQualifiedElement source, @NotNull ItemPresentation presentation) {
    super(source);
    m_presentation = presentation;
  }

  @Override
  public @NotNull String getPath() {
    return ORUtil.getQualifiedPath(m_source);
  }

  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return null;
  }

  @Override
  public @NotNull String getQualifiedName() {
    return m_source.getQualifiedName();
  }

  @Override
  public Icon getIcon(final int flags) {
    return m_presentation.getIcon(false);
  }

  @Override
  protected Icon getElementIcon(final int flags) {
    return m_presentation.getIcon(false);
  }

  @Override
  public ItemPresentation getPresentation() {
    return m_presentation;
  }
}
