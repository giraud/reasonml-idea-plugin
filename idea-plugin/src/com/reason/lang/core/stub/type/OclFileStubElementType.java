package com.reason.lang.core.stub.type;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.OclFile;
import com.reason.lang.core.stub.OclFileStub;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;

public class OclFileStubElementType extends IStubFileElementType<OclFileStub> {
    private static final int VERSION = 5;
    public static final IStubFileElementType<OclFileStub> INSTANCE = new OclFileStubElementType();

    private OclFileStubElementType() {
        super("OCAML_FILE", OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @NotNull
            @Override
            protected StubElement createStubForFile(@NotNull PsiFile file) {
                if (file instanceof OclFile) {
                    return new OclFileStub((OclFile) file);
                }
                return super.createStubForFile(file);
            }
        };
    }

    @Override
    public int getStubVersion() {
        return VERSION;
    }

    @Override
    public void serialize(@NotNull OclFileStub stub, @NotNull StubOutputStream dataStream) {
    }

    @NotNull
    @Override
    public OclFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) {
        return new OclFileStub(null);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "ocaml.FILE";
    }
}
