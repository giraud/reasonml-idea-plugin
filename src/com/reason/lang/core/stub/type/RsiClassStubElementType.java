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

public class RsiClassStubElementType extends ORStubElementType<RsiClassStub, RsiClass> {
    public RsiClassStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RsiClassImpl createPsi(@NotNull RsiClassStub stub) {
        return new RsiClassImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull RsiClassImpl createPsi(@NotNull ASTNode node) {
        return new RsiClassImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull RsiClassStub createStub(@NotNull RsiClass psi, @Nullable StubElement parentStub) {
        String[] path = psi.getPath();
        return new RsiClassStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path);
    }

    public void serialize(@NotNull RsiClassStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull RsiClassStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);

        return new RsiClassStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull RsiClassStub stub, @NotNull IndexSink sink) {
        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.CLASSES_FQN, fqn.hashCode());
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
