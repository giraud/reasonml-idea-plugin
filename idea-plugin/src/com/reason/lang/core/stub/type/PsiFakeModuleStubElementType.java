package com.reason.lang.core.stub.type;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.io.StringRef;
import com.reason.bs.BsConfigReader;
import com.reason.bs.BsPlatform;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypesUtil;

public class PsiFakeModuleStubElementType extends IStubElementType<PsiModuleStub, PsiFakeModule> {

    public PsiFakeModuleStubElementType(@NotNull String name, Language language) {
        super(name, language);
    }

    @NotNull
    public PsiFakeModule createPsi(@NotNull final PsiModuleStub stub) {
        return new PsiFakeModule(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiModuleStub createStub(@NotNull final PsiFakeModule fakeModule, final StubElement parentStub) {
        FileBase file = (FileBase) fakeModule.getContainingFile();

        // Finding if it's using a bs virtual namespace
        VirtualFile virtualFile = file.getViewProvider().getVirtualFile();
        VirtualFile originalFile = virtualFile instanceof LightVirtualFile ? ((LightVirtualFile) virtualFile).getOriginalFile() : virtualFile;
        String namespace = originalFile == null ? null :
                BsPlatform.findBsConfigForFile(file.getProject(), originalFile).map((bsConfig) -> BsConfigReader.read(bsConfig).getNamespace()).orElse(null);

        return new PsiModuleStub(parentStub, this, file.getModuleName(), namespace /*, isVirtualNamespace*/, null, file.isComponent(), file.isInterface());
    }

    public void serialize(@NotNull final PsiModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isComponent());
        dataStream.writeBoolean(stub.isInterface());
    }

    @NotNull
    public PsiModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String qname = dataStream.readUTFFast();
        boolean isComponent = dataStream.readBoolean();
        boolean isInterface = dataStream.readBoolean();

        return new PsiModuleStub(parentStub, this, moduleName, qname, null, isComponent, isInterface);
    }

    public void indexStub(@NotNull final PsiModuleStub stub, @NotNull final IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
        }

        sink.occurrence(IndexKeys.MODULES_TOP_LEVEL, name);

        String fqn = stub.getQualifiedName();
        sink.occurrence(IndexKeys.MODULES_FQN, fqn.hashCode());
        if (stub.isComponent()) {
            sink.occurrence(IndexKeys.MODULES_COMP, fqn);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage() + "." + super.toString();
    }
}
