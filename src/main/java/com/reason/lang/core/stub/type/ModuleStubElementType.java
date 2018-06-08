package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.MlTypes;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.core.psi.impl.PsiModuleImpl;
import com.reason.lang.core.stub.ModuleStub;
import com.reason.lang.ocaml.OclTypes;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ModuleStubElementType extends IStubElementType<ModuleStub, PsiModule> {

    public ModuleStubElementType(String name, Language language) {
        super(name, language);
    }

    public PsiModuleImpl createPsi(@NotNull final ModuleStub stub) {
        MlTypes types = getLanguage() instanceof RmlLanguage ? RmlTypes.INSTANCE : OclTypes.INSTANCE;
        return stub.isFileModule() ? new PsiFileModuleImpl(stub, this, types) : new PsiModuleImpl(stub, this, types);
    }

    @NotNull
    public ModuleStub createStub(@NotNull final PsiModule psi, final StubElement parentStub) {
        return new ModuleStub(parentStub, this, psi.getName(), psi.getQualifiedName(), psi.getAlias(), psi instanceof PsiFileModuleImpl, psi.isComponent());
    }

    public void serialize(@NotNull final ModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isFileModule());
        dataStream.writeBoolean(stub.isComponent());

        String alias = stub.getAlias();
        dataStream.writeBoolean(alias != null);
        if (alias != null) {
            dataStream.writeUTFFast(stub.getAlias());
        }
    }

    @NotNull
    public ModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String qname = dataStream.readUTFFast();
        boolean isFileModule = dataStream.readBoolean();
        boolean isComponent = dataStream.readBoolean();

        String alias = null;
        boolean isAlias = dataStream.readBoolean();
        if (isAlias) {
            alias = dataStream.readUTFFast();
        }

        return new ModuleStub(parentStub, this, moduleName, qname, alias, isFileModule, isComponent);
    }

    public void indexStub(@NotNull final ModuleStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
        }

        String fqn = stub.getQualifiedName();
        if (fqn != null) {
            sink.occurrence(IndexKeys.MODULES_FQN, fqn.hashCode());
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
