package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import org.jetbrains.annotations.*;

public class PsiModuleType extends ORCompositePsiElement {
    protected PsiModuleType(@NotNull IElementType elementType) {
        super(elementType);
    }
}
