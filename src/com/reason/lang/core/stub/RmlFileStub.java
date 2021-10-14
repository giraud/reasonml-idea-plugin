package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class RmlFileStub extends PsiFileStubImpl<FileBase> {
    private final boolean myIsComponent;

    public RmlFileStub(FileBase file, boolean isComponent) {
        super(file);
        myIsComponent = isComponent;
    }

    @Override
    public @NotNull IStubFileElementType<RmlFileStub> getType() {
        return RmlFileStubElementType.INSTANCE;
    }

    public boolean isComponent() {
        return myIsComponent;
    }
}
