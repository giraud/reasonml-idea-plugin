package com.reason.ide;

import com.intellij.lang.javascript.psi.*;
import com.intellij.psi.*;
import com.reason.bs.*;
import com.reason.lang.core.psi.impl.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class JsIconProvider extends com.intellij.ide.IconProvider {
  @Nullable
  @Override
  public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
    PsiElement element =
        psiElement instanceof PsiFakeModule ? psiElement.getContainingFile() : psiElement;
    if (element instanceof PsiFile) {
      if (isBsJsFile((PsiFile) element)) {
        return ORIcons.BS_FILE;
      }
    }

    return null;
  }

  /* needed as plugin.xml's filetype extension does NOT support extensions with multiple "." */
  private static boolean isBsJsFile(PsiFile psiFile) {
    if (psiFile instanceof JSFile) {
      JSFile jsFile = (JSFile) psiFile;
      return jsFile.getName().endsWith("." + BsConstants.BS_JS_FILE_EXTENSION);
    }
    return false;
  }
}
