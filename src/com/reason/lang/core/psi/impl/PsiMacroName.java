package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiMacroName extends ORCompositeTypePsiElement<ORTypes> {
    protected PsiMacroName(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
