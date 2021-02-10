package com.reason.lang.core.stub.type;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.NsFileStub;
import com.reason.lang.napkin.NsLanguage;

import java.io.*;

import org.jetbrains.annotations.NotNull;

public class NsFileStubElementType extends IStubFileElementType<NsFileStub> {
    private static final int VERSION = 4;
    public static final IStubFileElementType<NsFileStub> INSTANCE = new NsFileStubElementType();

    private NsFileStubElementType() {
        super("NAPKINSCRIPT_FILE", NsLanguage.INSTANCE);
    }

    @Override
    public @NotNull StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @Override
            protected @NotNull StubElement<FileBase> createStubForFile(@NotNull PsiFile file) {
                if (file instanceof NsFile) {
                    return new NsFileStub((NsFile) file, ((NsFile) file).isComponent());
                } else if (file instanceof NsInterfaceFile) {
                    return new NsFileStub((NsInterfaceFile) file, ((NsInterfaceFile) file).isComponent());
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
    public void serialize(@NotNull NsFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(stub.isComponent());
    }

    @Override
    public @NotNull NsFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new NsFileStub(null, dataStream.readBoolean());
    }

    @Override
    public @NotNull String getExternalId() {
        return "napkinscript.FILE";
    }
}
