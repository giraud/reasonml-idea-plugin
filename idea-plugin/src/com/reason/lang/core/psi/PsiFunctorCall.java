package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;

public class PsiFunctorCall extends PsiToken<ORTypes> {

    public PsiFunctorCall(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @NotNull
    public String getFunctorName() {
        String text = getText();

        PsiParameters params = PsiTreeUtil.findChildOfType(this, PsiParameters.class);
        if (params == null) {
            return text;
        }

        return text.substring(0, params.getTextOffset() - getTextOffset());
    }

    @NotNull
    @Override
    public String toString() {
        return "Functor call";
    }
}
