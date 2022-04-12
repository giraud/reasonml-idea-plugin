package com.reason.lang.core.psi.impl;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.*;
import org.jetbrains.annotations.NotNull;

public class PsiFunctionBody extends ORCompositePsiElement {
    protected PsiFunctionBody(IElementType type) {
        super(type);
    }
}
