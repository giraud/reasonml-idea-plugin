package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiKlassStub extends PsiQualifiedNameStub<PsiKlass> {
    public PsiKlassStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @NotNull String[] path) {
        super(parent, elementType, name, path);
    }

    public PsiKlassStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @NotNull String[] path) {
        super(parent, elementType, name, path);
    }
}
