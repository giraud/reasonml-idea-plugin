package com.reason.lang;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.reason.lang.core.psi.PsiRawMacroBody;
import org.jetbrains.annotations.NotNull;

public class JsInjector implements LanguageInjector {

    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host,
                                     @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof PsiRawMacroBody) {
            FileType jsFileType = FileTypeManager.getInstance().getFileTypeByExtension("js");
            if (jsFileType instanceof LanguageFileType) {
                Language jsLanguage = ((LanguageFileType) jsFileType).getLanguage();
                injectionPlacesRegistrar.addPlace(jsLanguage, new TextRange(1, host.getTextLength() - 1), null, null);
            }
        }
    }
}
