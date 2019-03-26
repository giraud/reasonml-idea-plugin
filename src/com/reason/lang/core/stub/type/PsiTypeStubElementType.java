package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.PsiTypeImpl;
import com.reason.lang.core.stub.PsiTypeStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PsiTypeStubElementType extends IStubElementType<PsiTypeStub, PsiType> {

    public PsiTypeStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    @NotNull
    public PsiTypeImpl createPsi(@NotNull final PsiTypeStub stub) {
        ORTypes types = getLanguage() instanceof RmlLanguage ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
        return new PsiTypeImpl(types, stub, this);
    }

    @NotNull
    public PsiTypeStub createStub(@NotNull final PsiType psi, final StubElement parentStub) {
        return new PsiTypeStub(parentStub, this, psi.getName(), psi.getQualifiedName());
    }

    public void serialize(@NotNull final PsiTypeStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
    }

    @NotNull
    public PsiTypeStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String qname = dataStream.readUTFFast();
        return new PsiTypeStub(parentStub, this, name, qname);
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
