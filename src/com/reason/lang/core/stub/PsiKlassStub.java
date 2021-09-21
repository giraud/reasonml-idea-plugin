package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class PsiKlassStub extends NamedStubBase<PsiKlass> {
    private final String[] myPath;
    private final String myQname;

    public PsiKlassStub(StubElement parent, @NotNull IStubElementType elementType, @NotNull String name, @NotNull String[] path) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
    }

    public PsiKlassStub(StubElement parent, @NotNull IStubElementType elementType, @NotNull StringRef name, @NotNull String[] path) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
    }

    public @NotNull String[] getPath() {
        return myPath;
    }

    public @NotNull String getQualifiedName() {
        return myQname;
    }
}
