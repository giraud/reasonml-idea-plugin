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

public class PsiValStubElementType extends ORStubElementType<PsiValStub, PsiVal> {
    public PsiValStubElementType(@Nullable Language language) {
        super("C_VAL_DECLARATION", language);
    }

    @NotNull
    public PsiValImpl createPsi(@NotNull ASTNode node) {
        return new PsiValImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiValImpl createPsi(@NotNull PsiValStub stub) {
        return new PsiValImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiValStub createStub(@NotNull PsiVal psi, StubElement parentStub) {
        String[] path = psi.getPath();
        return new PsiValStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path, psi.isFunction());
    }

    public void serialize(@NotNull PsiValStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isFunction());
    }

    @NotNull
    public PsiValStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        boolean isFunction = dataStream.readBoolean();

        return new PsiValStub(parentStub, this, name, path == null ? EMPTY_PATH : path, isFunction);
    }

    public void indexStub(@NotNull PsiValStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.VALS, name);
        }

        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.VALS_FQN, fqn.hashCode());
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
