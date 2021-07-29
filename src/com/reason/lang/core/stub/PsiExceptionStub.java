package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class PsiExceptionStub extends NamedStubBase<PsiException> {
    private final String[] myPath;
    private final @NotNull String myQname;
    private final @Nullable String myAlias;

    public PsiExceptionStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path, String alias) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
        myAlias = alias;
    }

    public PsiExceptionStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path, String alias) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
        myAlias = alias;
    }

    public String[] getPath() {
        return myPath;
    }

    public @NotNull String getQualifiedName() {
        return myQname;
    }

    public @Nullable String getAlias() {
        return myAlias;
    }
}
