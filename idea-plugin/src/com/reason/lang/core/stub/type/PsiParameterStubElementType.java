package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.impl.PsiParameterImpl;
import com.reason.lang.core.stub.PsiParameterStub;
import com.reason.lang.core.type.ORTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PsiParameterStubElementType extends IStubElementType<PsiParameterStub, PsiParameter> {

    public PsiParameterStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    @NotNull
    public PsiParameterImpl createPsi(@NotNull final PsiParameterStub stub) {
        return new PsiParameterImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiParameterStub createStub(@NotNull final PsiParameter psi, final StubElement parentStub) {
        return new PsiParameterStub(parentStub, this, psi.getName(), psi.getQualifiedName());
    }

    public void serialize(@NotNull final PsiParameterStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
    }

    @NotNull
    public PsiParameterStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String qname = dataStream.readUTFFast();
        return new PsiParameterStub(parentStub, this, name, qname);
    }

    public void indexStub(@NotNull final PsiParameterStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.PARAMETERS, name);
        }

        String fqn = stub.getQualifiedName();
        if (fqn != null) {
            sink.occurrence(IndexKeys.PARAMETERS_FQN, fqn.hashCode());
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
