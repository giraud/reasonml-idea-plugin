package com.reason.lang.core.stub.type;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.ResFileStub;
import com.reason.lang.rescript.ResLanguage;

import java.io.*;

import org.jetbrains.annotations.NotNull;

public class ResFileStubElementType extends IStubFileElementType<ResFileStub> {
    private static final int VERSION = 8;
    public static final IStubFileElementType<ResFileStub> INSTANCE = new ResFileStubElementType();

    private ResFileStubElementType() {
        super("RESCRIPT_FILE", ResLanguage.INSTANCE);
    }

    @Override
    public @NotNull StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected @NotNull PsiFileStub<? extends PsiFile> createStubForFile(@NotNull PsiFile file) {
                if (file instanceof ResFile) {
                    return new ResFileStub((ResFile) file, ((ResFile) file).isComponent());
                } else if (file instanceof ResInterfaceFile) {
                    return new ResFileStub((ResInterfaceFile) file, ((ResInterfaceFile) file).isComponent());
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
    public void serialize(@NotNull ResFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(stub.isComponent());
    }

    @Override
    public @NotNull ResFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new ResFileStub(null, dataStream.readBoolean());
    }

    @Override
    public @NotNull String getExternalId() {
        return "rescript.FILE";
    }
}
