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

public class PsiParameterStubElementType extends ORStubElementType<PsiParameterStub, PsiParameter> {
    public PsiParameterStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    @NotNull
    public PsiParameterDeclaration createPsi(@NotNull PsiParameterStub stub) {
        ORTypes types = ORTypesUtil.getInstance(getLanguage());
        return stub.isNamed() ? new PsiNamedParameterDeclaration(types, stub, this) : new PsiParameterDeclaration(types, stub, this);
    }

    @NotNull
    public PsiParameterDeclaration createPsi(@NotNull ASTNode node) {
        ORTypes types = ORTypesUtil.getInstance(getLanguage());
        boolean isNamed = node.getElementType() == types.C_NAMED_PARAM_DECLARATION;
        return isNamed ? new PsiNamedParameterDeclaration(types, node) : new PsiParameterDeclaration(types, node);
    }

    @NotNull
    public PsiParameterStub createStub(@NotNull PsiParameter psi, StubElement parentStub) {
        return new PsiParameterStub(parentStub, this, psi.getName(), psi.getPath(), psi.getQualifiedName(), psi instanceof PsiNamedParameterDeclaration);
    }

    public void serialize(@NotNull PsiParameterStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isNamed());
    }

    @NotNull
    public PsiParameterStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        String qname = dataStream.readUTFFast();
        boolean isNamed = dataStream.readBoolean();
        return new PsiParameterStub(parentStub, this, name, path, qname, isNamed);
    }

    public void indexStub(@NotNull PsiParameterStub stub, @NotNull IndexSink sink) {
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
        return getLanguage().getID() + "." + super.toString();
    }
}
