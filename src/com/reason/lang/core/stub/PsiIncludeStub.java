package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiIncludeStub extends StubBase<PsiInclude> {
    @Nullable private final StringRef myFileModule;
    private final String myIncludePath;
    private final String[] myQualifiedPath;
    private final String[] myResolvedPath;

    public PsiIncludeStub(StubElement parent, IStubElementType elementType, @Nullable StringRef fileModule, String includePath, String[] qualifiedPath, String[] resolvedPath) {
        super(parent, elementType);
        myFileModule = fileModule;
        myIncludePath = includePath;
        myQualifiedPath = qualifiedPath;
        myResolvedPath = resolvedPath;
    }

    public PsiIncludeStub(StubElement parent, IStubElementType elementType, @Nullable String fileModule, String includePath, String[] qualifiedPath, String[] resolvedPath) {
        super(parent, elementType);
        myFileModule = StringRef.fromString(fileModule);
        myIncludePath = includePath;
        myQualifiedPath = qualifiedPath;
        myResolvedPath = resolvedPath;
    }

    public String getIncludePath() {
        return myIncludePath;
    }

    public String[] getQualifiedPath() {
        return myQualifiedPath;
    }

    @Nullable
    public String getFileModule() {
        return myFileModule != null ? myFileModule.getString() : null;
    }

    public String[] getResolvedPath() {
        return myResolvedPath;
    }
}
