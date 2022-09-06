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

public class PsiRecordFieldStubElementType extends ORStubElementType<PsiRecordFieldStub, PsiRecordField> {
    public PsiRecordFieldStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    public @NotNull PsiRecordFieldImpl createPsi(@NotNull PsiRecordFieldStub stub) {
        return new PsiRecordFieldImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull PsiRecordFieldImpl createPsi(@NotNull ASTNode node) {
        return new PsiRecordFieldImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull PsiRecordFieldStub createStub(@NotNull PsiRecordField psi, @Nullable StubElement parentStub) {
        return new PsiRecordFieldStub(parentStub, this, psi.getName(), psi.getPath());
    }

    public void serialize(@NotNull final PsiRecordFieldStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull PsiRecordFieldStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        return new PsiRecordFieldStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull PsiRecordFieldStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.RECORD_FIELDS, name);
        }
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
