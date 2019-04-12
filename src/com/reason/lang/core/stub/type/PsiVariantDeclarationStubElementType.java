package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.stub.PsiVariantDeclarationStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PsiVariantDeclarationStubElementType extends IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> {

    public PsiVariantDeclarationStubElementType(@NotNull String name, Language language) {
        super(name, language);
    }

    @NotNull
    public PsiVariantDeclaration createPsi(@NotNull final PsiVariantDeclarationStub stub) {
        ORTypes types = getLanguage() instanceof RmlLanguage ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
        return new PsiVariantDeclaration(types, stub, this);
    }

    @NotNull
    public PsiVariantDeclarationStub createStub(@NotNull final PsiVariantDeclaration psi, final StubElement parentStub) {
        return new PsiVariantDeclarationStub(parentStub, this, psi.getName(), psi.getQualifiedName());
    }

    public void serialize(@NotNull final PsiVariantDeclarationStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
    }

    @NotNull
    public PsiVariantDeclarationStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String qname = dataStream.readUTFFast();

        return new PsiVariantDeclarationStub(parentStub, this, moduleName, qname);
    }

    public void indexStub(@NotNull final PsiVariantDeclarationStub stub, @NotNull final IndexSink sink) {
        String fqn = stub.getQualifiedName();
        if (fqn != null) {
            sink.occurrence(IndexKeys.VARIANTS_FQN, fqn.hashCode());
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
