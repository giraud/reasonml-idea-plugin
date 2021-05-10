package com.reason.lang.core.stub.type;

import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class RmlFileStubElementType extends IStubFileElementType<RmlFileStub> {
    private static final int VERSION = 7;
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
    public void serialize(@NotNull RmlFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(stub.isComponent());
    }

    @NotNull
    @Override
    public RmlFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RmlFileStub(null, dataStream.readBoolean());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "reason.FILE";
    }
}
