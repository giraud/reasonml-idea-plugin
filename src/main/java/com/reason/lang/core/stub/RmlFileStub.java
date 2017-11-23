package com.reason.lang.core.stub;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.RmlFile;
import com.reason.lang.core.stub.type.RmlFileElementType;

public class RmlFileStub extends PsiFileStubImpl<RmlFile> {
    public RmlFileStub(RmlFile file) {
        super(file);
    }

    @Override
    public IStubFileElementType getType() {
        return RmlFileElementType.INSTANCE;
    }

}
