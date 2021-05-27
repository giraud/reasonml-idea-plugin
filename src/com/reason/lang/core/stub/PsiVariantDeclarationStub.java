package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiVariantDeclarationStub extends NamedStubBase<PsiVariantDeclaration> {
    private final String[] myPath;
    private final @NotNull String myQname;

    public PsiVariantDeclarationStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
    }

    public PsiVariantDeclarationStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
    }

    public String[] getPath() {
        return myPath;
    }

    public @NotNull String getQualifiedName() {
        return myQname;
    }
}
