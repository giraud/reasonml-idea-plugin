package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiObjectField extends PsiToken<ORTypes> implements PsiLanguageConverter {

    public PsiObjectField(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
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

        String convertedText;

        if (language == OclLanguage.INSTANCE) {
            // Convert from Reason to OCaml
            convertedText = getText();
        } else {
            // Convert from OCaml to Reason
            PsiElement nameIdentifier = getNameIdentifier();
            if (nameIdentifier == null) {
                convertedText = getText();
            } else {
                String valueAsText = "";
                PsiElement value = getValue();
                if (value instanceof PsiLanguageConverter) {
                    valueAsText = ((PsiLanguageConverter) value).asText(language);
                } else if (value != null) {
                    valueAsText = value.getText();
                }

                convertedText = "\"" + nameIdentifier.getText() + "\": " + valueAsText;
            }
        }

        return convertedText;
    }

    @Nullable
    private PsiElement getValue() {
        PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
        return colon == null ? null : ORUtil.nextSiblingNode(colon.getNode()).getPsi();
    }

    @NotNull
    @Override
    public String toString() {
        return "ObjectField";
    }
}
