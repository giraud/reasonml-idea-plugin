package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public class PsiObjectFieldStub extends PsiQualifiedNameStub<PsiObjectField> {
    public PsiObjectFieldStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @NotNull String[] path) {
        super(parent, elementType, name, path);
    }

    public PsiObjectFieldStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @NotNull String[] path) {
        super(parent, elementType, name, path);
    }
}
