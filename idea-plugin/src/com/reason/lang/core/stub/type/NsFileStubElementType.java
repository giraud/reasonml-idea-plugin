package com.reason.lang.core.stub.type;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.NsFile;
import com.reason.ide.files.NsInterfaceFile;
import com.reason.lang.core.stub.NsFileStub;
import com.reason.lang.napkin.NsLanguage;

public class NsFileStubElementType extends IStubFileElementType<NsFileStub> {
    private static final int VERSION = 2;
    public static final IStubFileElementType<NsFileStub> INSTANCE = new NsFileStubElementType();

    private NsFileStubElementType() {
        super("NAPKINSCRIPT_FILE", NsLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @NotNull
            @Override
            protected StubElement createStubForFile(@NotNull PsiFile file) {
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

    @NotNull
    @Override
    public NsFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new NsFileStub(null, dataStream.readBoolean());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "napkinscript.FILE";
    }
}
