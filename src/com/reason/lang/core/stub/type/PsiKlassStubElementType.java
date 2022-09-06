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

public class PsiKlassStubElementType extends ORStubElementType<PsiKlassStub, PsiKlass> {
    public PsiKlassStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull PsiKlassImpl createPsi(@NotNull PsiKlassStub stub) {
        return new PsiKlassImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull PsiKlassImpl createPsi(@NotNull ASTNode node) {
        return new PsiKlassImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull PsiKlassStub createStub(@NotNull PsiKlass psi, @Nullable StubElement parentStub) {
        String[] path = psi.getPath();
        return new PsiKlassStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path);
    }

    public void serialize(@NotNull PsiKlassStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull PsiKlassStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);

        return new PsiKlassStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull PsiKlassStub stub, @NotNull IndexSink sink) {
        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.CLASSES_FQN, fqn.hashCode());
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
