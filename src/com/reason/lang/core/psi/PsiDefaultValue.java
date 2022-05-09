package com.reason.lang.core.psi;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import org.jetbrains.annotations.*;

public class PsiDefaultValue extends ORCompositePsiElement {
    public PsiDefaultValue(@NotNull IElementType elementType) {
        super(elementType);
    }
}
