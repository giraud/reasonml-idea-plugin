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

public class PsiExceptionStubElementType extends ORStubElementType<PsiExceptionStub, PsiException> {
    public static final int VERSION = 7;

    public PsiExceptionStubElementType(Language language) {
        super("C_EXCEPTION_DECLARATION", language);
    }

    @NotNull
    public PsiException createPsi(@NotNull PsiExceptionStub stub) {
        return new PsiExceptionImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiException createPsi(@NotNull ASTNode node) {
        return new PsiExceptionImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiExceptionStub createStub(@NotNull PsiException psi, StubElement parentStub) {
        String[] path = psi.getPath();
        return new PsiExceptionStub(parentStub, this, psi.getName(), path == null ? EMPTY_PATH : path, psi.getAlias());
    }

    public void serialize(@NotNull PsiExceptionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeName(stub.getAlias());
    }

    @NotNull
    public PsiExceptionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        StringRef alias = dataStream.readName();

        return new PsiExceptionStub(parentStub, this, name, path, alias == null ? null : alias.getString());
    }

    public void indexStub(@NotNull PsiExceptionStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.EXCEPTIONS, name);
        }

        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.EXCEPTIONS_FQN, fqn.hashCode());
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
