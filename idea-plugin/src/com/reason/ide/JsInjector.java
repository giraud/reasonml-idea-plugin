package com.reason.ide;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.reason.lang.core.psi.PsiRawBody;
import org.jetbrains.annotations.NotNull;

public class JsInjector implements LanguageInjector {

  public void getLanguagesToInject(
      @NotNull PsiLanguageInjectionHost host,
      @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
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
