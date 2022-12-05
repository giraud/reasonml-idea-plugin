package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiIncludeStub extends StubBase<RPsiInclude> {
    @Nullable private final StringRef myFileModule;
    private final String myIncludePath;
    private final String[] myQualifiedPath;

    public PsiIncludeStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef fileModule, String includePath, String[] qualifiedPath) {
        super(parent, elementType);
        myFileModule = fileModule;
        myIncludePath = includePath;
        myQualifiedPath = qualifiedPath;
    }

    public PsiIncludeStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String fileModule, String includePath, String[] qualifiedPath) {
        super(parent, elementType);
        myFileModule = StringRef.fromString(fileModule);
        myIncludePath = includePath;
        myQualifiedPath = qualifiedPath;
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
}
