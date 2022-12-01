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

public class PsiValStubElementType extends ORStubElementType<PsiValStub, RPsiVal> {
    public PsiValStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiValImpl createPsi(@NotNull ASTNode node) {
        return new RPsiValImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull RPsiValImpl createPsi(@NotNull PsiValStub stub) {
        return new RPsiValImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull PsiValStub createStub(@NotNull RPsiVal psi, @Nullable StubElement parentStub) {
        String[] path = psi.getPath();
        return new PsiValStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path, psi.isFunction());
    }

    public void serialize(@NotNull PsiValStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isFunction());
    }

    public @NotNull PsiValStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
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

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
