package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiExceptionStub extends PsiQualifiedNameStub<RPsiException> {
    private final String myAlias;

    public PsiExceptionStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @NotNull String[] path, @Nullable String alias) {
        super(parent, elementType, name, path);
        myAlias = alias;
    }

    public PsiExceptionStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @NotNull String[] path, @Nullable String alias) {
        super(parent, elementType, name, path);
        myAlias = alias;
    }

    public @Nullable String getAlias() {
        return myAlias;
    }
}
