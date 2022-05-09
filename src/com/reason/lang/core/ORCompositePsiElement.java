package com.reason.lang.core;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;

public abstract class ORCompositePsiElement extends CompositePsiElement {
    protected ORCompositePsiElement(IElementType type) {
        super(type);
    }

    @Override
    public String toString() {
        return "ORComposite(" + getElementType() + ")";
    }
}
