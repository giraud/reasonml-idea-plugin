package com.reason.lang.core.stub.type;

import com.intellij.psi.stubs.*;
import com.reason.lang.RmlLanguage;
import com.reason.lang.core.psi.Type;
import com.reason.lang.core.psi.TypeImpl;
import com.reason.lang.core.stub.TypeStub;
import com.reason.lang.core.stub.index.RmlStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TypeStubElementType extends IStubElementType<TypeStub, Type> {
    public TypeStubElementType(@NotNull String debugName) {
        super(debugName, RmlLanguage.INSTANCE);
    }

    @Override
    public Type createPsi(@NotNull TypeStub stub) {
        return new TypeImpl(stub, this);
    }

    @NotNull
    @Override
    public TypeStub createStub(@NotNull Type psi, StubElement parentStub) {
        return new TypeStub(parentStub, this, psi.getName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "reason." + super.toString();
    }

    @Override
    public void serialize(@NotNull TypeStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public TypeStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new TypeStub(parentStub, this, dataStream.readName());
    }

    @Override
    public void indexStub(@NotNull TypeStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(RmlStubIndexKeys.ALL_NAMES, name);
        }
    }
}
