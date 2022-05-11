package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiVariantStubElementType extends ORStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> {
    public PsiVariantStubElementType(@Nullable Language language) {
        super("C_VARIANT_DECLARATION", language);
    }

    @NotNull
    public PsiVariantDeclaration createPsi(@NotNull PsiVariantDeclarationStub stub) {
        return new PsiVariantDeclaration(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiVariantDeclaration createPsi(@NotNull ASTNode node) {
        return new PsiVariantDeclaration(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiVariantDeclarationStub createStub(@NotNull PsiVariantDeclaration psi, StubElement parentStub) {
        return new PsiVariantDeclarationStub(parentStub, this, psi.getName(), psi.getPath());
    }

    public void serialize(@NotNull final PsiVariantDeclarationStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    @NotNull
    public PsiVariantDeclarationStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);

        return new PsiVariantDeclarationStub(parentStub, this, moduleName, path);
    }

    public void indexStub(@NotNull final PsiVariantDeclarationStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.VARIANTS, name);
        }

        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.VARIANTS_FQN, fqn.hashCode());
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
