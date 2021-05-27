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

public class PsiExternalStubElementType extends ORStubElementType<PsiExternalStub, PsiExternal> {
    public static final int VERSION = 8;

    public PsiExternalStubElementType(@Nullable Language language) {
        super("C_EXTERNAL_DECLARATION", language);
    }

    @NotNull
    public PsiExternalImpl createPsi(@NotNull PsiExternalStub stub) {
        return new PsiExternalImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiExternalImpl createPsi(@NotNull ASTNode node) {
        return new PsiExternalImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiExternalStub createStub(@NotNull PsiExternal psi, StubElement parentStub) {
        return new PsiExternalStub(parentStub, this, psi.getName(), psi.getPath(), psi.isFunction());
    }

    public void serialize(@NotNull PsiExternalStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isFunction());
    }

    @NotNull
    public PsiExternalStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        boolean isFunction = dataStream.readBoolean();

        return new PsiExternalStub(parentStub, this, name, path, isFunction);
    }

    public void indexStub(@NotNull PsiExternalStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.EXTERNALS, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
