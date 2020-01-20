package com.reason.lang.core.stub.type;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.impl.PsiInnerModuleImpl;
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
    public PsiModuleStub createStub(@NotNull final PsiFakeModule psi, final StubElement parentStub) {
        return new PsiModuleStub(parentStub, this, psi.getName(), psi.getQualifiedName(), psi.getAlias(), psi.isComponent());
    }

    public void serialize(@NotNull final PsiModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        dataStream.writeUTFFast(stub.getQualifiedName());
        dataStream.writeBoolean(stub.isComponent());
    }

    @NotNull
    public PsiModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String qname = dataStream.readUTFFast();
        boolean isComponent = dataStream.readBoolean();
        String alias = null;

        return new PsiModuleStub(parentStub, this, moduleName, qname, alias, isComponent);
    }

    public void indexStub(@NotNull final PsiModuleStub stub, @NotNull final IndexSink sink) {
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
