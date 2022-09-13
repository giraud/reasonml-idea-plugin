package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiFunSwitch extends ORCompositePsiElement<ORTypes> {
    protected PsiFunSwitch(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull List<PsiPatternMatch> getPatterns() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiPatternMatch.class);
    }
}
