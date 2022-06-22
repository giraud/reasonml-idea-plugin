package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiJsObject extends ORCompositePsiElement<ORTypes> implements PsiLanguageConverter {
    protected PsiJsObject(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull Collection<PsiObjectField> getFields() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiObjectField.class);
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang) {
            convertedText = new StringBuilder();
            boolean firstField = true;
            if (toLang == OclLanguage.INSTANCE) {
                // Convert to OCaml
                convertedText.append("<");
                for (PsiElement element : getChildren()) {
                    if (element instanceof PsiObjectField) {
                        if (firstField) {
                            firstField = false;
                        } else {
                            convertedText.append("; ");
                        }
                        convertedText.append(((PsiObjectField) element).asText(toLang));
                    }
                }
                convertedText.append("> Js.t");
            } else {
                // Convert from OCaml
                convertedText.append("{. ");
                for (PsiElement element : getChildren()) {
                    if (element instanceof PsiObjectField) {
                        if (firstField) {
                            firstField = false;
                        } else {
                            convertedText.append(", ");
                        }
                        convertedText.append(((PsiObjectField) element).asText(toLang));
                    }
                }
                convertedText.append(" }");
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }
}
