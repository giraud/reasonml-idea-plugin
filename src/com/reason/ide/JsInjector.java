package com.reason.ide;

import com.intellij.lang.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public class JsInjector implements LanguageInjector {

  public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
    if (host instanceof PsiRawBody) {
      FileType jsFileType = FileTypeManager.getInstance().getFileTypeByExtension("js");
      if (jsFileType instanceof LanguageFileType) {
        Language jsLanguage = ((LanguageFileType) jsFileType).getLanguage();
        PsiRawBody macroHost = (PsiRawBody) host;
        TextRange macroTextRange = macroHost.getMacroTextRange();
        injectionPlacesRegistrar.addPlace(jsLanguage, macroTextRange, null, null);
      }
    }
  }
}
