package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiTypeStub extends PsiQualifiedNameStub<PsiType> {
    private final boolean myAbstract;
    private final boolean myJsObject;
    private final boolean myRecord;

    public PsiTypeStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @Nullable String[] path, boolean isAbstract, boolean isJsObject, boolean record) {
        super(parent, elementType, name, path);
        myAbstract = isAbstract;
        myJsObject = isJsObject;
        myRecord = record;
    }

    public PsiTypeStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @Nullable String[] path, boolean isAbstract, boolean isJsObject, boolean isRecord) {
        super(parent, elementType, name, path);
        myAbstract = isAbstract;
        myJsObject = isJsObject;
        myRecord = isRecord;
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
