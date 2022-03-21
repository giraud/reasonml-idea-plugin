package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiSwitchImpl extends CompositeTypePsiElement<ORTypes> implements PsiSwitch {
    protected PsiSwitchImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Nullable
    public PsiBinaryCondition getCondition() {
        return PsiTreeUtil.findChildOfType(this, PsiBinaryCondition.class);
    }

    @Override
    public @NotNull List<PsiPatternMatch> getPatterns() {
        PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
        return ORUtil.findImmediateChildrenOfClass(scope == null ? this : scope, PsiPatternMatch.class);
    }
}
