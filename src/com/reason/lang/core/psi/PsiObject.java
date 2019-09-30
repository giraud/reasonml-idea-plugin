package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class PsiObject extends ASTWrapperPsiElement implements PsiLanguageConverter {

    public PsiObject(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String asText(@NotNull Language language) {
        if (getLanguage() == language) {
            return getText();
        }

        String convertedText = null;

        if (language == OclLanguage.INSTANCE) {
            // Convert from Reason to OCaml
            convertedText = getText();
        } else {
            // Convert from OCaml to Reason
            convertedText = "{. " + getText().substring(1, getTextLength() - 1) + " }";
        }


        return convertedText == null ? getText() : convertedText;
    }

    @NotNull
    @Override
    public String toString() {
        return "Object";
    }

}
