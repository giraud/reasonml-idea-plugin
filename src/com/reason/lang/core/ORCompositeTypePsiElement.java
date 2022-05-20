package com.reason.lang.core;

import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public abstract class ORCompositeTypePsiElement<T> extends CompositePsiElement {
    protected final @NotNull T m_types;

    protected ORCompositeTypePsiElement(@NotNull T types, @NotNull IElementType elementType) {
        super(elementType);
        m_types = types;
    }

    @Override
    public String toString() {
        String name = getName();
        String className = getClass().getSimpleName();
        boolean isImpl = className.endsWith("Impl");
        return (isImpl ? className.substring(0, className.length() - 4) : className) + (name == null ? "" : ":" + name);
    }
}
