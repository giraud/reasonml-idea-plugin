package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiBinaryCondition extends ORCompositeTypePsiElement<ORTypes> {

    protected PsiBinaryCondition(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
