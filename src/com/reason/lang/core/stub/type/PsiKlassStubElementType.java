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
    public PsiKlassStubElementType(@Nullable Language language) {
        super("C_CLASS_DECLARATION", language);
    }

    @NotNull
    public PsiKlassImpl createPsi(@NotNull PsiKlassStub stub) {
        return new PsiKlassImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiKlassImpl createPsi(@NotNull ASTNode node) {
        return new PsiKlassImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiKlassStub createStub(@NotNull PsiKlass psi, StubElement parentStub) {
        String[] path = psi.getPath();
        return new PsiKlassStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path);
    }

    public void serialize(@NotNull PsiKlassStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    @NotNull
    public PsiKlassStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);

        return new PsiKlassStub(parentStub, this, name, path);
    }

    public void indexStub(@NotNull PsiKlassStub stub, @NotNull IndexSink sink) {
        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.CLASSES_FQN, fqn.hashCode());
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
