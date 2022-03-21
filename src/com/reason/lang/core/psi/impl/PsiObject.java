package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class PsiObject extends ORCompositePsiElement implements PsiLanguageConverter {
    protected PsiObject(@NotNull IElementType type) {
        super(type);
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang) {
            if (toLang != OclLanguage.INSTANCE) {
                convertedText = new StringBuilder();
                convertedText.append("{. ").append(getText(), 1, getTextLength() - 1).append(" }");
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }
}
