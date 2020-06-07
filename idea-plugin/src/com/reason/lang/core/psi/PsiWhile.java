package com.reason.lang.core.psi;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.PsiToken;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiWhile extends PsiToken<ORTypes> {

    public PsiWhile(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    public PsiBinaryCondition getCondition() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiBinaryCondition.class);
    }

    @Nullable
    public PsiScopedExpr getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "While";
    }
}
