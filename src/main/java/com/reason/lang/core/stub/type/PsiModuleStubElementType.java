package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.io.*;

public abstract class PsiModuleStubElementType extends ORStubElementType<PsiModuleStub, RPsiModule> {
    protected PsiModuleStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    @NotNull
    public PsiModuleStub createStub(@NotNull RPsiModule psi, StubElement parentStub) {
        boolean isFunctorCall = false;
        boolean isModuleType = false;
        String alias = null;
        String signatureName = null;
        if (psi instanceof RPsiInnerModule innerModule) {
            isModuleType = innerModule.isModuleType();
            isFunctorCall = innerModule.isFunctorCall();
            alias = innerModule.getAlias();
            RPsiModuleSignature moduleSignature = innerModule.getModuleSignature();
            signatureName = moduleSignature != null ? moduleSignature.getName() : null;
        }

        return new PsiModuleStub(parentStub, this, psi.getName(), psi.getPath(), null, alias, psi.isComponent(), isModuleType, false, isFunctorCall, signatureName);
    }

    public void serialize(@NotNull PsiModuleStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isComponent());
        dataStream.writeBoolean(stub.isModuleType());
        dataStream.writeBoolean(stub.isTopLevel());
        dataStream.writeBoolean(stub.isFunctorCall());

        String alias = stub.getAlias();
        dataStream.writeBoolean(alias != null);
        if (alias != null) {
            dataStream.writeUTFFast(alias);
        }

        String returnTypeName = stub.getSignatureName();
        dataStream.writeBoolean(returnTypeName != null);
        if (returnTypeName != null) {
            dataStream.writeName(returnTypeName);
        }
    }

    public @NotNull PsiModuleStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        boolean isComponent = dataStream.readBoolean();
        boolean isModuleType = dataStream.readBoolean();
        boolean isTopLevel = dataStream.readBoolean();
        boolean isFunctorCall = dataStream.readBoolean();

        String alias = null;
        boolean isAlias = dataStream.readBoolean();
        if (isAlias) {
            alias = dataStream.readUTFFast();
        }

        StringRef returnTypeName = null;
        if (dataStream.readBoolean()) {
            returnTypeName = dataStream.readName();
        }

        return new PsiModuleStub(parentStub, this, moduleName, path, null, alias, isComponent, isModuleType, isTopLevel, isFunctorCall, returnTypeName);
    }

    public void indexStub(@NotNull PsiModuleStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
            int fqnHash = stub.getQualifiedName().hashCode();
            sink.occurrence(IndexKeys.MODULES_FQN, fqnHash);
            String signatureName = stub.getSignatureName();
            if (signatureName != null) {
                sink.occurrence(IndexKeys.MODULES_SIGNATURE, signatureName);
            }
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
