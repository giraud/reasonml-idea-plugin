package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiObjectField extends ASTWrapperPsiElement implements PsiLanguageConverter {

    public PsiObjectField(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
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
            PsiElement nameIdentifier = getNameIdentifier();
            convertedText = "\"" + nameIdentifier.getText() + "\"" + getText().substring(nameIdentifier.getTextLength(), getTextLength());
        }


        return convertedText == null ? getText() : convertedText;
    }

    @NotNull
    @Override
    public String toString() {
        return "ObjectField";
    }
}
