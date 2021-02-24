package com.reason.lang.core.stub.type;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.RmlFile;
import com.reason.ide.files.RmlInterfaceFile;
import com.reason.lang.core.stub.RmlFileStub;
import com.reason.lang.reason.RmlLanguage;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

public class RmlFileStubElementType extends IStubFileElementType<RmlFileStub> {
    private static final int VERSION = 6;
    public static final IStubFileElementType<RmlFileStub> INSTANCE = new RmlFileStubElementType();

    private RmlFileStubElementType() {
        super("REASON_FILE", RmlLanguage.INSTANCE);
    }

    @Override
    public @NotNull StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected @NotNull PsiFileStub<? extends PsiFile> createStubForFile(@NotNull PsiFile file) {
                if (file instanceof RmlFile) {
                    return new RmlFileStub((RmlFile) file, ((RmlFile) file).isComponent());
                } else if (file instanceof RmlInterfaceFile) {
                    return new RmlFileStub((RmlInterfaceFile) file, ((RmlInterfaceFile) file).isComponent());
                }
                return new PsiFileStubImpl<>(file);
            }
        };
    }

    @Override
    public int getStubVersion() {
        return VERSION;
    }

    @Override
    public void serialize(@NotNull RmlFileStub stub, @NotNull StubOutputStream dataStream)
            throws IOException {
        dataStream.writeBoolean(stub.isComponent());
    }

    @NotNull
    @Override
    public RmlFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub)
            throws IOException {
        return new RmlFileStub(null, dataStream.readBoolean());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "reason.FILE";
    }
}
