package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class PsiOption extends ORCompositeTypePsiElement<ORTypes> implements PsiLanguageConverter {
    protected PsiOption(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang && toLang != null) {
            convertedText = new StringBuilder();

            if (toLang == OclLanguage.INSTANCE) {
                // Convert from Reason/Rescript to OCaml
                PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
                if (scope != null) {
                    String scopeText = scope.getText();
                    convertedText.append(scopeText, 1, scopeText.length() - 1).append(" option");
                }
            } else if (fromLang == OclLanguage.INSTANCE) {
                // Convert from OCaml
                PsiElement[] children = getChildren();
                if (children.length > 2) {
                    int lastChild = children[children.length - 2] instanceof PsiWhiteSpace ? children.length - 3 : children.length - 2;

                    convertedText.append("option");
                    convertedText.append(toLang.getTemplateStart());
                    for (int i = 0; i <= lastChild; i++) {
                        PsiElement child = children[i];
                        IElementType childElementType = child.getNode().getElementType();
                        if (childElementType != m_types.OPTION) {
                            convertedText.append(child.getText());
                        }
                    }
                    convertedText.append(toLang.getTemplateEnd());
                } else {
                    convertedText.append(getText());
                }
            } else {
                convertedText.append(getText());
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }
}
