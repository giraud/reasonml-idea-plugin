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

public class RPsiRecordFieldStubElementType extends ORStubElementType<RsiRecordFieldStub, RPsiRecordField> {
    public RPsiRecordFieldStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    public @NotNull RPsiRecordFieldImpl createPsi(@NotNull RsiRecordFieldStub stub) {
        return new RPsiRecordFieldImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull RPsiRecordFieldImpl createPsi(@NotNull ASTNode node) {
        return new RPsiRecordFieldImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull RsiRecordFieldStub createStub(@NotNull RPsiRecordField psi, @Nullable StubElement parentStub) {
        String[] path = psi.getPath();
        return new RsiRecordFieldStub(parentStub, this, psi.getName(), path != null ? path : new String[0]);
    }

    public void serialize(@NotNull final RsiRecordFieldStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull RsiRecordFieldStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        return new RsiRecordFieldStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull RsiRecordFieldStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.RECORD_FIELDS, name);
        }
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
