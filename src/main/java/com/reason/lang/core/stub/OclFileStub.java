package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class OclFileStub extends PsiFileStubImpl<FileBase> {
    public OclFileStub(FileBase file) {
        super(file);
    }

    @Override
    public @NotNull IStubFileElementType<OclFileStub> getType() {
        return OclFileStubElementType.INSTANCE;
    }
}
