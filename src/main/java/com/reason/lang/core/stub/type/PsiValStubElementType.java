package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.impl.PsiValImpl;
import com.reason.lang.core.psi.type.MlTypes;
import com.reason.lang.core.stub.PsiValStub;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PsiValStubElementType extends IStubElementType<PsiValStub, PsiVal> {

    public PsiValStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public PsiValImpl createPsi(@NotNull PsiValStub stub) {
        MlTypes types = getLanguage() instanceof RmlLanguage ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
        return new PsiValImpl(types, stub, this);
    }

    @NotNull
    public PsiValStub createStub(@NotNull PsiVal psi, StubElement parentStub) {
        return new PsiValStub(parentStub, this, psi.getName());
    }

    public void serialize(@NotNull PsiValStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    public PsiValStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new PsiValStub(parentStub, this, dataStream.readName());
    }

    public void indexStub(@NotNull PsiValStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.VALS, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
