package com.reason.ide.go;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiElementBase;
import com.reason.lang.core.psi.PsiQualifiedElement;
import javax.swing.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ORDelegatePsiElement extends PsiElementBase {
  protected final @NotNull PsiQualifiedElement m_source;

  protected ORDelegatePsiElement(@NotNull PsiQualifiedElement source) {
    m_source = source;
  }

  @Override
  public @NotNull PsiElement getOriginalElement() {
    return m_source;
  }

  @Nullable
  @Override
  public PsiElement getParent() {
    return m_source.getParent();
  }

  @Override
  public @NotNull PsiElement getNavigationElement() {
    return m_source.getNavigationElement();
  }

  @Override
  @NotNull
  public Language getLanguage() {
    return m_source.getLanguage();
  }

  @Override
  public PsiElement @NotNull [] getChildren() {
    return m_source.getChildren();
  }

  @Override
  @Nullable
  public PsiElement getFirstChild() {
    return m_source.getFirstChild();
  }

  @Override
  @Nullable
  public PsiElement getLastChild() {
    return m_source.getLastChild();
  }

  @Override
  @Nullable
  public PsiElement getNextSibling() {
    return m_source.getNextSibling();
  }

  @Override
  @Nullable
  public PsiElement getPrevSibling() {
    return m_source.getPrevSibling();
  }

  @Override
  @Nullable
  public TextRange getTextRange() {
    return m_source.getTextRange();
  }

  @Override
  public int getStartOffsetInParent() {
    return m_source.getStartOffsetInParent();
  }

  @Override
  public int getTextLength() {
    return m_source.getTextLength();
  }

  @Override
  @Nullable
  public PsiElement findElementAt(int offset) {
    return m_source.findElementAt(offset);
  }

  @Override
  public int getTextOffset() {
    return m_source.getTextOffset();
  }

  @Override
  @Nullable
  @NonNls
  public String getText() {
    return m_source.getText();
  }

  @NotNull
  @Override
  public TextRange getTextRangeInParent() {
    return m_source.getTextRangeInParent();
  }

  @Override
  public char @NotNull [] textToCharArray() {
    return m_source.textToCharArray();
  }

  @Override
  public boolean textContains(char c) {
    return m_source.textContains(c);
  }

  @Override
  @Nullable
  public ASTNode getNode() {
    return m_source.getNode();
  }

  @Override
  public @Nullable Icon getIcon(final int flags) {
    return m_source.getIcon(flags);
  }

  @Override
  public PsiManager getManager() {
    return m_source.getManager();
  }

  @Override
  public boolean isPhysical() {
    return false;
  }
}
