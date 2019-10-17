package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.impl.PsiRecordFieldImpl;
import com.reason.lang.core.stub.PsiRecordFieldStub;
import com.reason.lang.core.type.ORTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PsiRecordFieldStubElementType extends IStubElementType<PsiRecordFieldStub, PsiRecordField> {

    public PsiRecordFieldStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    @NotNull
    public PsiRecordFieldImpl createPsi(@NotNull final PsiRecordFieldStub stub) {
        return new PsiRecordFieldImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiRecordFieldStub createStub(@NotNull final PsiRecordField psi, final StubElement parentStub) {
        return new PsiRecordFieldStub(parentStub, this, psi.getName(), psi.getQualifiedName());
    }

    public void serialize(@NotNull final PsiRecordFieldStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
    }

    @NotNull
    public PsiRecordFieldStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String qname = dataStream.readUTFFast();
        return new PsiRecordFieldStub(parentStub, this, name, qname);
    }

    public void indexStub(@NotNull final PsiRecordFieldStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.RECORD_FIELDS, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
