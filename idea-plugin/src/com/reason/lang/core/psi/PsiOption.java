package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class PsiOption extends PsiToken<ORTypes> implements PsiLanguageConverter {

    public PsiOption(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
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
            PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
            if (scope != null) {
                String scopeText = scope.getText();
                convertedText = scopeText.substring(1, scopeText.length() - 1) + " option";
            }
        }

        // Convert from OCaml

        return convertedText == null ? getText() : convertedText;
    }
}
