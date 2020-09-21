package com.reason.lang.core.signature;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiSignatureUtil {

  private PsiSignatureUtil() {}

  @NotNull
  public static String getSignature(
      @Nullable PsiElement element, @NotNull Language targetLanguage) {
    if (element instanceof PsiExternal) {
      return ((PsiExternal) element).getORSignature().asString(targetLanguage);
    } else if (element instanceof PsiLet) {
      PsiLet let = (PsiLet) element;
      ORSignature signature = let.hasInferredType() ? let.getInferredType() : let.getORSignature();
      return signature.asString(targetLanguage);
    } else if (element instanceof PsiVal) {
      PsiVal val = (PsiVal) element;
      ORSignature signature = val.getORSignature();
      return signature.asString(targetLanguage);
    } else if (element instanceof PsiInnerModule) {
      return ((PsiInnerModule) element).getQualifiedName();
    }
    return "";
  }
}
