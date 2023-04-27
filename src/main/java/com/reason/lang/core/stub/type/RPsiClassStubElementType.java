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

public class RPsiClassStubElementType extends ORStubElementType<RsiClassStub, RPsiClass> {
    public RPsiClassStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiClassImpl createPsi(@NotNull RsiClassStub stub) {
        return new RPsiClassImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull RPsiClassImpl createPsi(@NotNull ASTNode node) {
        return new RPsiClassImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull RsiClassStub createStub(@NotNull RPsiClass psi, @Nullable StubElement parentStub) {
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
