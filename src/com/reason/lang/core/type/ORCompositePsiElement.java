package com.reason.lang.core.type;

import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public abstract class ORCompositePsiElement<T> extends CompositePsiElement {
    protected final @NotNull T myTypes;

    protected ORCompositePsiElement(@NotNull T types, @NotNull IElementType elementType) {
        super(elementType);
        myTypes = types;
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        boolean isImpl = className.endsWith("Impl");
        return (isImpl ? className.substring(0, className.length() - 4) : className);
    }
}
