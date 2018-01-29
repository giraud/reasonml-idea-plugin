package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.core.psi.impl.PsiModuleImpl;
import com.reason.lang.core.stub.ModuleStub;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ModuleStubElementType extends IStubElementType<ModuleStub, PsiModule> {

    public ModuleStubElementType(String name, Language language) {
        super(name, language);
    }

    public PsiModuleImpl createPsi(@NotNull final ModuleStub stub) {
        return stub.isFileModule() ? new PsiFileModuleImpl(stub, this) : new PsiModuleImpl(stub, this);
    }

    @NotNull
    public ModuleStub createStub(@NotNull final PsiModule psi, final StubElement parentStub) {
        return new ModuleStub(parentStub, this, psi.getName(), psi.getQualifiedName(), psi instanceof PsiFileModuleImpl);
    }

    public void serialize(@NotNull final ModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isFileModule());
    }

    @NotNull
    public ModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String qname = dataStream.readUTFFast();
        boolean isFileModule = dataStream.readBoolean();

        return new ModuleStub(parentStub, this, moduleName, qname, isFileModule);
    }

    public void indexStub(@NotNull final ModuleStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
