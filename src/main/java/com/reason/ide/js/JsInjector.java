package com.reason.ide.js;

import com.intellij.lang.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public class JsInjector implements LanguageInjector {
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof RPsiMacroBody body) {
            PsiElement parent = body.getParent();
            if (parent instanceof RPsiMacro macro) {
                String name = macro.getName();
                if ("%raw".equals(name) || "%%raw".equals(name)) {
                    FileType jsFileType = FileTypeManager.getInstance().getFileTypeByExtension("js");
                    if (jsFileType instanceof LanguageFileType jsLanguageFileType) {
                        Language jsLanguage = jsLanguageFileType.getLanguage();
                        RPsiMacroBody macroHost = (RPsiMacroBody) host;
                        TextRange macroTextRange = macroHost.getMacroTextRange();
                        if (macroTextRange != null) {
                            injectionPlacesRegistrar.addPlace(jsLanguage, macroTextRange, null, null);
                        }
                    }
                }
            }
        }
    }
}
