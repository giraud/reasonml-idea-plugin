package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public class PsiVariantDeclarationStub extends PsiQualifiedNameStub<PsiVariantDeclaration> {
    public PsiVariantDeclarationStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @Nullable String[] path) {
        super(parent, elementType, name, path);
    }

    public PsiVariantDeclarationStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @Nullable String[] path) {
        super(parent, elementType, name, path);
    }
}
