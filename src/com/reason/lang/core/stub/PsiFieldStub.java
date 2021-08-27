package com.reason.lang.core.stub;

import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public abstract class PsiFieldStub<T extends PsiNamedElement> extends NamedStubBase<T> {
    private final String[] myPath;
    private final String myQname;

    PsiFieldStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @NotNull String[] path) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
    }

    PsiFieldStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @NotNull String[] path) {
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
