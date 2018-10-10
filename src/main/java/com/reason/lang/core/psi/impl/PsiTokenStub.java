package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class PsiTokenStub<T, S extends StubElement> extends StubBasedPsiElementBase<S> {

    protected final T m_types;

    public PsiTokenStub(@NotNull T types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiTokenStub(@NotNull T types, @NotNull S stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
        m_types = types;
    }
}
