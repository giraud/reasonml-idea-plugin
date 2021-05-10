package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiTypeStub extends NamedStubBase<PsiType> {
    private final String[] myPath;
    private final @NotNull String myQname;
    private final boolean myAbstract;
    private final boolean myJsObject;
    private final boolean myRecord;

    public PsiTypeStub(StubElement parent, @NotNull IStubElementType elementType, String name, String[] path, boolean isAbstract, boolean isJsObject, boolean record) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + getName();
        myAbstract = isAbstract;
        myJsObject = isJsObject;
        myRecord = record;
    }

    public PsiTypeStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String[] path, boolean isAbstract, boolean isJsObject, boolean isRecord) {
        super(parent, elementType, name);
        myPath = path;
        myQname = Joiner.join(".", path) + "." + getName();
        myAbstract = isAbstract;
        myJsObject = isJsObject;
        myRecord = isRecord;
    }

    public String[] getPath() {
        return myPath;
    }

    public @NotNull String getQualifiedName() {
        return myQname;
    }

    public boolean isAbstract() {
        return myAbstract;
    }

    public boolean isJsObject() {
        return myJsObject;
    }

    public boolean isRecord() {
        return myRecord;
    }
}
