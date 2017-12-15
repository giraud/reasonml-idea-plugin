package com.reason.lang.core.stub.type;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.RmlFile;
import com.reason.lang.RmlLanguage;
import com.reason.lang.core.stub.RmlFileStub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RmlFileStubElementType extends IStubFileElementType<RmlFileStub> {
    private static final int VERSION = 1;
    public static final IStubFileElementType INSTANCE = new RmlFileStubElementType();

    private RmlFileStubElementType() {
        super("REASON_FILE", RmlLanguage.INSTANCE);
    }

    @Override
    public StubBuilder getBuilder() {
        return new DefaultStubBuilder() {
            @NotNull
            @Override
            protected StubElement createStubForFile(@NotNull PsiFile file) {
                if (file instanceof RmlFile) {
                    return new RmlFileStub((RmlFile) file);
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
    public void serialize(@NotNull RmlFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    }

    @NotNull
    @Override
    public RmlFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RmlFileStub(null);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "reason.FILE";
    }
}
