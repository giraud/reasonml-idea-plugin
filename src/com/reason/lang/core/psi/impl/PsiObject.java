package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public class PsiObject extends ORCompositeTypePsiElement<ORTypes> implements PsiLanguageConverter {
    protected PsiObject(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang) {
            if (toLang != null && toLang != OclLanguage.INSTANCE) {
                convertedText = new StringBuilder();
                convertedText.append("{. ");
                List<String> conversions = getFields().stream().map(item -> item.asText(toLang)).collect(Collectors.toList());
                convertedText.append(Joiner.join(toLang.getParameterSeparator(), conversions));
                convertedText.append(" }");
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }

    public @NotNull List<PsiObjectField> getFields() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiObjectField.class);
    }
}
