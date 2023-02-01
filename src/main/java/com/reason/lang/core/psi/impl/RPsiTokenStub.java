package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import org.jetbrains.annotations.*;

public class RPsiTokenStub<T, P extends PsiElement, S extends StubElement<P>> extends StubBasedPsiElementBase<S> {
    @NotNull protected final T myTypes;

    public RPsiTokenStub(@NotNull T types, @NotNull ASTNode node) {
        super(node);
        myTypes = types;
    }

    public RPsiTokenStub(@NotNull T types, @NotNull S stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
        myTypes = types;
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        boolean isImpl = className.endsWith("Impl");
        return (isImpl ? className.substring(0, className.length() - 4) : className);
    }
}
