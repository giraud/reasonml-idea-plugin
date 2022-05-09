package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiTypeConstraint extends ORCompositeTypePsiElement<ORTypes> {
    protected PsiTypeConstraint(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
