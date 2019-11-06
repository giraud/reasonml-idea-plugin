package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PsiJsObject extends ASTWrapperPsiElement implements PsiLanguageConverter {

    public PsiJsObject(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @NotNull
    public Collection<PsiObjectField> getFields() {
        return PsiTreeUtil.findChildrenOfType(this, PsiObjectField.class);
    }

    @NotNull
    @Override
    public String asText(@NotNull Language language) {
        if (getLanguage() == language) {
            return getText();
        }

        String convertedText;

        if (language == OclLanguage.INSTANCE) {
            // Convert from Reason to OCaml
            convertedText = getText();
        } else {
            // Convert from OCaml to Reason
            convertedText = "";
            for (int i = 0; i < getChildren().length; i++) {
                PsiElement element = getChildren()[i];
                if (element instanceof PsiObjectField) {
                    if (0 < i) {
                        convertedText += ", ";
                    }
                    convertedText += ((PsiObjectField) element).asText(language);
                }
            }
            convertedText = "{. " + convertedText + " }";
        }


        return convertedText == null ? getText() : convertedText;
    }

    @NotNull
    @Override
    public String toString() {
        return "JsObject";
    }

}
