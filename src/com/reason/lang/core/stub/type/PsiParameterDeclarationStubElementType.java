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

public class PsiParameterDeclarationStubElementType extends ORStubElementType<PsiParameterDeclarationStub, RPsiParameterDeclaration> {
    public PsiParameterDeclarationStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    @NotNull
    public RPsiParameterDeclaration createPsi(@NotNull PsiParameterDeclarationStub stub) {
        ORTypes types = ORTypesUtil.getInstance(getLanguage());
        return new RPsiParameterDeclarationImpl(types, stub, this);
    }

    @NotNull
    public RPsiParameterDeclaration createPsi(@NotNull ASTNode node) {
        ORTypes types = ORTypesUtil.getInstance(getLanguage());
        return new RPsiParameterDeclarationImpl(types, node);
    }

    @NotNull
    public PsiParameterDeclarationStub createStub(@NotNull RPsiParameterDeclaration psi, StubElement parentStub) {
        return new PsiParameterDeclarationStub(parentStub, this, psi.getName(), psi.getPath(), psi.getQualifiedName(), psi.isNamed());
    }

    public void serialize(@NotNull PsiParameterDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isNamed());
    }

    @NotNull
    public PsiParameterDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        String qname = dataStream.readUTFFast();
        boolean isNamed = dataStream.readBoolean();
        return new PsiParameterDeclarationStub(parentStub, this, name, path, qname, isNamed);
    }

    public void indexStub(@NotNull PsiParameterDeclarationStub stub, @NotNull IndexSink sink) {
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
