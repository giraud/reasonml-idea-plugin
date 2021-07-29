package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

public class PsiRecordFieldStub extends NamedStubBase<PsiRecordField> {
    private final String[] myPath;
    private final @NotNull String myQname;

    public PsiRecordFieldStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + name;
    }

    public PsiRecordFieldStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path) {
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
