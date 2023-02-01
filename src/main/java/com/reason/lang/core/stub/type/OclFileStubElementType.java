package com.reason.lang.core.stub.type;

import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class OclFileStubElementType extends IStubFileElementType<OclFileStub> {
    public static final IStubFileElementType<OclFileStub> INSTANCE = new OclFileStubElementType();

    private OclFileStubElementType() {
        super("OCAML_FILE", OclLanguage.INSTANCE);
    }

    @Override
    public @NotNull StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected @NotNull PsiFileStub<? extends PsiFile> createStubForFile(@NotNull PsiFile file) {
                if (file instanceof OclFile) {
                    return new OclFileStub((OclFile) file);
                } else if (file instanceof OclInterfaceFile) {
                    return new OclFileStub((OclInterfaceFile) file);
                }
                return new PsiFileStubImpl<>(file);
            }
        };
    }

    @Override
    public int getStubVersion() {
        return ORStubVersions.OCL_FILE;
    }

    @Override
    public void serialize(@NotNull OclFileStub stub, @NotNull StubOutputStream dataStream) {
    }

    @Override
    public @NotNull OclFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) {
        return new OclFileStub(null);
    }

    @Override
    public @NotNull String getExternalId() {
        return "ocaml.FILE";
    }
}
