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

public class RPsiClassMethodStubElementType extends ORStubElementType<RsiClassMethodStub, RPsiClassMethod> {
    public RPsiClassMethodStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiClassMethodImpl createPsi(@NotNull RsiClassMethodStub stub) {
        return new RPsiClassMethodImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull RPsiClassMethodImpl createPsi(@NotNull ASTNode node) {
        return new RPsiClassMethodImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull RsiClassMethodStub createStub(@NotNull RPsiClassMethod psi, @Nullable StubElement parentStub) {
        String[] path = psi.getPath();
        return new RsiClassMethodStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path);
    }

    public void serialize(@NotNull RsiClassMethodStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull RsiClassMethodStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);

        return new RsiClassMethodStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull RsiClassMethodStub stub, @NotNull IndexSink sink) {
        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.CLASS_METHODS_FQN, fqn.hashCode());
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
