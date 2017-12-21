package com.reason.lang.core.stub.type;

import java.io.*;

import com.intellij.util.io.StringRef;
import com.reason.lang.core.ModulePath;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.RmlLanguage;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiModuleImpl;
import com.reason.lang.core.stub.ModuleStub;

public class ModuleStubElementType extends IStubElementType<ModuleStub, PsiModule> {

    public ModuleStubElementType(String name) {
        super(name, RmlLanguage.INSTANCE);
    }

    public PsiModuleImpl createPsi(@NotNull final ModuleStub stub) {
        return new PsiModuleImpl(stub, this);
    }

    @NotNull
    public ModuleStub createStub(@NotNull final PsiModule psi, final StubElement parentStub) {
        return new ModuleStub(parentStub, this, psi.getName(), psi.getModulePath());
    }

    public void serialize(@NotNull final ModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        String[] moduleQN = stub.getModulePath().getNames();
        dataStream.writeInt(moduleQN.length);
        for (String name : moduleQN) {
            dataStream.writeUTFFast(name);
        }

    }

    @NotNull
    public ModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        int moduleQNLength = dataStream.readInt();
        String[] moduleQN = new String[moduleQNLength];
        for (int i = 0; i < moduleQNLength; i++) {
            moduleQN[i] = dataStream.readUTFFast();

        }
        ModulePath modulePath = new ModulePath(moduleQN);

        return new ModuleStub(parentStub, this, moduleName, modulePath);
    }

    public void indexStub(@NotNull final ModuleStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return "reason." + super.toString();
    }
}
