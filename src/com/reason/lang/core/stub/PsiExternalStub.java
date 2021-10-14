package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiExternalStub extends PsiQualifiedNameStub<PsiExternal> {
    private final boolean myIsFunction;

    public PsiExternalStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @NotNull String[] path, boolean isFunction) {
        super(parent, elementType, name, path);
        myIsFunction = isFunction;
    }

    public PsiExternalStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @NotNull String[] path, boolean isFunction) {
        super(parent, elementType, name, path);
        myIsFunction = isFunction;
    }

    public boolean isFunction() {
        return myIsFunction;
    }
}
