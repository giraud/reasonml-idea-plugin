package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiBinaryCondition;
import com.reason.lang.core.psi.PsiPatternMatch;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class PsiSwitchImpl extends PsiToken<ORTypes> implements PsiSwitch {

    public PsiSwitchImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Nullable
    public PsiBinaryCondition getCondition() {
        return PsiTreeUtil.findChildOfType(this, PsiBinaryCondition.class);
    }

    @NotNull
    @Override
    public Collection<?> getPatterns() {
        List<PsiScopedExpr> scope = ORUtil.findImmediateChildrenOfClass(this, PsiScopedExpr.class);
        return ORUtil.findImmediateChildrenOfClass(scope.get(0), PsiPatternMatch.class);
    }

    @NotNull
    @Override
    public String toString() {
        return "Switch";
    }
}
