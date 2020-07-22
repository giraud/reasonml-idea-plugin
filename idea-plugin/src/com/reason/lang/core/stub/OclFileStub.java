package com.reason.lang.core.stub;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.OclFile;
import com.reason.lang.core.stub.type.OclFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class OclFileStub extends PsiFileStubImpl<OclFile> {

    public OclFileStub(OclFile file) {
        super(file);
    }

    @NotNull
    @Override
    public IStubFileElementType<OclFileStub> getType() {
        return OclFileStubElementType.INSTANCE;
    }
}
