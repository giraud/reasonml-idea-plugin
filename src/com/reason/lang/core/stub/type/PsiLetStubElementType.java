package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiLetImpl;
import com.reason.lang.core.stub.PsiLetStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class PsiLetStubElementType extends IStubElementType<PsiLetStub, PsiLet> {

    public PsiLetStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    @NotNull
    public PsiLetImpl createPsi(@NotNull PsiLetStub stub) {
        ORTypes types = getLanguage() instanceof RmlLanguage ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
        return new PsiLetImpl(types, stub, this);
    }

    @NotNull
    public PsiLetStub createStub(@NotNull PsiLet psi, StubElement parentStub) {
        return new PsiLetStub(parentStub, this, psi.getName(), psi.getQualifiedName(), psi.isFunction());
    }

    public void serialize(@NotNull PsiLetStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isFunction());
    }

    @NotNull
    public PsiLetStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String qname = dataStream.readUTFFast();
        boolean isFunction = dataStream.readBoolean();

        return new PsiLetStub(parentStub, this, name, qname, isFunction);
    }

    public void indexStub(@NotNull PsiLetStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.LETS, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
