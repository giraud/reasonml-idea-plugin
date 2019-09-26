package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class PsiTypeConstrName extends ASTWrapperPsiElement implements PsiLanguageConverter {

    //region Constructors
    public PsiTypeConstrName(@NotNull ASTNode node) {
        super(node);
    }
    //endregion


    @NotNull
    @Override
    public String toString() {
        return "Type constr name";
    }

    public boolean hasParameters() {
        ASTNode[] children = getNode().getChildren(null);
        return 1 < children.length;
    }

    @NotNull
    @Override
    public String asText(@NotNull Language language) {
        if (getLanguage() == language || !hasParameters()) {
            return getText();
        }

        String convertedText = "";
        PsiElement child = getFirstChild();
        PsiElement nextSibling = child.getNextSibling();

        if (language == OclLanguage.INSTANCE) {
            // Convert from Reason to OCaml
            String typeName = child.getText();
            child = nextSibling;
            if (child instanceof PsiScopedExpr) {
                child = child.getFirstChild().getNextSibling();
            }
            nextSibling = child.getNextSibling();
            int parameters = 0;
            while (true) {
                convertedText += child.getText();
                parameters++;

                child = nextSibling;
                nextSibling = child == null ? null : child.getNextSibling();
                if (nextSibling == null) {
                    break;
                }
            }

            boolean hasParentesis = 1 < parameters;
            convertedText = (hasParentesis ? "(" : "") + convertedText.trim() + (hasParentesis ? ") " : " ") + typeName;
        } else {
            // Convert from OCaml to Reason
            while (true) {
                convertedText += child.getText();

                child = nextSibling;
                nextSibling = child.getNextSibling();
                if (nextSibling == null) {
                    break;
                }
            }

            boolean isScopedExpression = (getFirstChild() instanceof PsiScopedExpr);
            convertedText = child.getText() + (isScopedExpression ? "" : "(") + convertedText.trim() + (isScopedExpression ? "" : ")");
        }

        return convertedText;
    }
}
