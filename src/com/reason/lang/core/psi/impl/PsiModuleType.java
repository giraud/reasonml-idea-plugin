package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiModuleType extends ORCompositeTypePsiElement<ORTypes> {
    protected PsiModuleType(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
