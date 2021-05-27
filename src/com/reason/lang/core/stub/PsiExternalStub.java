package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiExternalStub extends NamedStubBase<PsiExternal> {
    private final String[] myPath;
    private final @NotNull String myQname;
    private final boolean myIsFunction;

    public PsiExternalStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path, boolean isFunction) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
        myIsFunction = isFunction;
    }

    public PsiExternalStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path, boolean isFunction) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
        myIsFunction = isFunction;
    }

    public String[] getPath() {
        return myPath;
    }

    public @NotNull String getQualifiedName() {
        return myQname;
    }

    public boolean isFunction() {
        return myIsFunction;
    }
}
