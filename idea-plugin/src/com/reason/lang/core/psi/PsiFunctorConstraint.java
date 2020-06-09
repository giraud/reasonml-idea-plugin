package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;

public class PsiFunctorConstraint extends PsiToken<ORTypes> {

    public PsiFunctorConstraint(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @NotNull
    @Override
    public String toString() {
        return "Functor constraint";
    }
}
