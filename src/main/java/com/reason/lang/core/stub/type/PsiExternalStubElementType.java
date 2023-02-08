package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiExternalStubElementType extends ORStubElementType<PsiExternalStub, RPsiExternal> {
    public PsiExternalStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiExternalImpl createPsi(@NotNull PsiExternalStub stub) {
        return new RPsiExternalImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull RPsiExternalImpl createPsi(@NotNull ASTNode node) {
        return new RPsiExternalImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull PsiExternalStub createStub(@NotNull RPsiExternal psi, @Nullable StubElement parentStub) {
        String[] path = psi.getPath();
        return new PsiExternalStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path, psi.isFunction());
    }

    public void serialize(@NotNull PsiExternalStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isFunction());
    }

    public @NotNull PsiExternalStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        boolean isFunction = dataStream.readBoolean();

        return new PsiExternalStub(parentStub, this, name, path == null ? EMPTY_PATH : path, isFunction);
    }

    public void indexStub(@NotNull PsiExternalStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.EXTERNALS, name);
        }
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
