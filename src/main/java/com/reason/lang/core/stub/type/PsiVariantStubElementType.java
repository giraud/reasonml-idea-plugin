package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiVariantStubElementType extends ORStubElementType<PsiVariantDeclarationStub, RPsiVariantDeclaration> {
    public PsiVariantStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiVariantDeclaration createPsi(@NotNull PsiVariantDeclarationStub stub) {
        return new RPsiVariantDeclaration(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull RPsiVariantDeclaration createPsi(@NotNull ASTNode node) {
        return new RPsiVariantDeclaration(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull PsiVariantDeclarationStub createStub(@NotNull RPsiVariantDeclaration psi, @Nullable StubElement parentStub) {
        return new PsiVariantDeclarationStub(parentStub, this, psi.getName(), psi.getPath());
    }

    public void serialize(@NotNull PsiVariantDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull PsiVariantDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);

        return new PsiVariantDeclarationStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull PsiVariantDeclarationStub stub, @NotNull IndexSink sink) {
        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.VARIANTS_FQN, fqn.hashCode());
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
