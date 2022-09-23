package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiSwitch extends ORCompositePsiElement<ORTypes> {
    protected RPsiSwitch(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiBinaryCondition getCondition() {
        return PsiTreeUtil.findChildOfType(this, RPsiBinaryCondition.class);
    }

    public @NotNull List<RPsiPatternMatch> getPatterns() {
        RPsiSwitchBody scope = ORUtil.findImmediateFirstChildOfClass(this, RPsiSwitchBody.class);
        return ORUtil.findImmediateChildrenOfClass(scope == null ? this : scope, RPsiPatternMatch.class);
    }
}
