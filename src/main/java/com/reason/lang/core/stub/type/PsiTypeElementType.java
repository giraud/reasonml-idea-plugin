package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.PsiTypeImpl;
import com.reason.lang.core.stub.PsiTypeStub;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PsiTypeElementType extends IStubElementType<PsiTypeStub, PsiType> {

    private final MlTypes m_types;

    public PsiTypeElementType(String name, Language language, MlTypes types) {
        super(name, language);
        m_types = types;
    }

    public PsiTypeImpl createPsi(@NotNull final PsiTypeStub stub) {
        return new PsiTypeImpl(m_types == null ? RmlTypes.INSTANCE : m_types, stub, this);
    }

    @NotNull
    public PsiTypeStub createStub(@NotNull final PsiType psi, final StubElement parentStub) {
        return new PsiTypeStub(parentStub, this, psi.getName());
    }

    public void serialize(@NotNull final PsiTypeStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    public PsiTypeStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        return new PsiTypeStub(parentStub, this, dataStream.readName());
    }

    public void indexStub(@NotNull final PsiTypeStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.TYPES, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
