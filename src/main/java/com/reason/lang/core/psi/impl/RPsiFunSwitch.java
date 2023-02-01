package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiFunSwitch extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiFunSwitch(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @NotNull List<RPsiPatternMatch> getPatterns() {
        return ORUtil.findImmediateChildrenOfClass(this, RPsiPatternMatch.class);
    }
}
