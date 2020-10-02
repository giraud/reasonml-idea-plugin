package com.reason.ide.template;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiUtilCore;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RmlContextType extends TemplateContextType {

  RmlContextType(
      @NotNull String id,
      @NotNull String presentableName,
      @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(id, presentableName, baseContextType);
  }

  @Override
  public boolean isInContext(@NotNull PsiFile file, int offset) {
    if (!PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(RmlLanguage.INSTANCE)) {
      return false;
    }

    PsiElement element = file.findElementAt(offset);
    if (element instanceof PsiWhiteSpace) {
      return false;
    }

    return element != null && isInContext(element);
  }

  protected abstract boolean isInContext(PsiElement element);

  public static class Generic extends RmlContextType {
    protected Generic() {
      super("REASON_CODE", "Reason", EverywhereContextType.class);
    }

    @Override
    protected boolean isInContext(PsiElement element) {
      return true;
    }
  }
}
