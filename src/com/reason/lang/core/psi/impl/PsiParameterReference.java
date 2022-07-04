package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class PsiParameterReference extends ORCompositePsiElement<ORTypes> {
    protected PsiParameterReference(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
