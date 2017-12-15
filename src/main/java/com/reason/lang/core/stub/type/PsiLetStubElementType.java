package com.reason.lang.core.stub.type;

import com.intellij.psi.stubs.*;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.RmlLanguage;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiLetImpl;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PsiLetStubElementType extends IStubElementType<PsiLetStub, PsiLet> {

    public PsiLetStubElementType(String name) {
        super(name, RmlLanguage.INSTANCE);
    }

    public PsiLetImpl createPsi(@NotNull final PsiLetStub stub) {
        return new PsiLetImpl(stub, this);
    }

    @NotNull
    public PsiLetStub createStub(@NotNull final PsiLet psi, final StubElement parentStub) {
        return new PsiLetStub(parentStub, this, psi.getName());
    }

    public void serialize(@NotNull final PsiLetStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    public PsiLetStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        return new PsiLetStub(parentStub, this, dataStream.readName());
    }

    public void indexStub(@NotNull final PsiLetStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.LETS, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return "reason." + super.toString();
    }
}
