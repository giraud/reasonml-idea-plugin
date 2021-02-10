package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.stub.PsiModuleStub;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;

public abstract class PsiModuleStubElementType extends ORStubElementType<PsiModuleStub, PsiModule> {

    public PsiModuleStubElementType(@NotNull String name, Language language) {
        super(name, language);
    }

    @NotNull
    public PsiModuleStub createStub(@NotNull final PsiModule psi, final StubElement parentStub) {
        return new PsiModuleStub(
                parentStub,
                this,
                psi.getName(),
                psi.getPath(),
                psi.getAlias(),
                psi.isComponent(),
                psi.isInterface());
    }

    public void serialize(
            @NotNull final PsiModuleStub stub, @NotNull final StubOutputStream dataStream)
            throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getPath());
        dataStream.writeBoolean(stub.isComponent());
        dataStream.writeBoolean(stub.isInterface());

        String alias = stub.getAlias();
        dataStream.writeBoolean(alias != null);
        if (alias != null) {
            dataStream.writeUTFFast(stub.getAlias());
        }
    }

    @NotNull
    public PsiModuleStub deserialize(
            @NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String path = dataStream.readUTFFast();
        boolean isComponent = dataStream.readBoolean();
        boolean isInterface = dataStream.readBoolean();

        String alias = null;
        boolean isAlias = dataStream.readBoolean();
        if (isAlias) {
            alias = dataStream.readUTFFast();
        }

        return new PsiModuleStub(parentStub, this, moduleName, path, alias, isComponent, isInterface);
    }

    public void indexStub(@NotNull final PsiModuleStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
            if (stub.isComponent()) {
                sink.occurrence(IndexKeys.MODULES_COMP, name);
            }
        }

        int fqnHash = stub.getQualifiedName().hashCode();
        sink.occurrence(IndexKeys.MODULES_FQN, fqnHash);
        if (stub.isComponent()) {
            sink.occurrence(IndexKeys.MODULES_COMP_FQN, fqnHash);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
