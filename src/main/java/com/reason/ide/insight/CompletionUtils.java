package com.reason.ide.insight;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletionUtils {
  public static final int KEYWORD_PRIORITY = 10;

  private CompletionUtils() {
  }

  @Nullable
  static IElementType getPrevNodeType(@NotNull PsiElement element) {
    PsiElement prevLeaf = PsiTreeUtil.prevVisibleLeaf(element);
    return prevLeaf == null ? null : prevLeaf.getNode().getElementType();
  }
}
