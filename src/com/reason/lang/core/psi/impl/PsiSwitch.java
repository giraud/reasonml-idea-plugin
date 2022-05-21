package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiSwitch extends ORCompositePsiElement<ORTypes> {
    protected PsiSwitch(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable PsiBinaryCondition getCondition() {
        return PsiTreeUtil.findChildOfType(this, PsiBinaryCondition.class);
    }

    public @NotNull List<PsiPatternMatch> getPatterns() {
        PsiSwitchBody scope = ORUtil.findImmediateFirstChildOfClass(this, PsiSwitchBody.class);
        return ORUtil.findImmediateChildrenOfClass(scope == null ? this : scope, PsiPatternMatch.class);
    }
}
