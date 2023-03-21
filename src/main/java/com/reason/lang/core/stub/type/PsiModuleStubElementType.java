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
        if (psi instanceof RPsiInnerModule) {
            isFunctorCall = ((RPsiInnerModule) psi).isFunctorCall();
        }

        return new PsiModuleStub(parentStub, this, psi.getName(), psi.getPath(), psi.getQualifiedNameAsPath(), null, psi.getAlias(), psi.isComponent(), psi.isInterface(), psi instanceof RPsiFakeModule, isFunctorCall);
    }

    public void serialize(@NotNull PsiModuleStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        SerializerUtil.writePath(dataStream, stub.getQualifiedNameAsPath());
        dataStream.writeBoolean(stub.isComponent());
        dataStream.writeBoolean(stub.isInterface());
        dataStream.writeBoolean(stub.isTopLevel());
        dataStream.writeBoolean(stub.isFunctorCall());

        String alias = stub.getAlias();
        dataStream.writeBoolean(alias != null);
        if (alias != null) {
            dataStream.writeUTFFast(stub.getAlias());
        }
    }

    public @NotNull PsiModuleStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef moduleName = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        String[] qNamePath = SerializerUtil.readPath(dataStream);
        boolean isComponent = dataStream.readBoolean();
        boolean isInterface = dataStream.readBoolean();
        boolean isTopLevel = dataStream.readBoolean();
        boolean isFunctorCall = dataStream.readBoolean();

        String alias = null;
        boolean isAlias = dataStream.readBoolean();
        if (isAlias) {
            alias = dataStream.readUTFFast();
        }

        return new PsiModuleStub(parentStub, this, moduleName, path, qNamePath, null, alias, isComponent, isInterface, isTopLevel, isFunctorCall);
    }

    public void indexStub(@NotNull PsiModuleStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.MODULES, name);
            if (stub.isTopLevel()) {
                sink.occurrence(IndexKeys.MODULES_TOP_LEVEL, name);
            }
            if (stub.isComponent()) {
                sink.occurrence(IndexKeys.MODULES_COMP, name);
            }
            String alias = stub.getAlias();
            if (alias != null) {
                int pos = alias.indexOf(".");
                String topName = pos < 0 ? alias : alias.substring(0, pos);
                sink.occurrence(IndexKeys.MODULES_ALIASED, topName);
                sink.occurrence(IndexKeys.MODULES_ALIASES, stub.getQualifiedName());
            }
        }

        int fqnHash = stub.getQualifiedName().hashCode();
        sink.occurrence(IndexKeys.MODULES_FQN, fqnHash);
        if (stub.isFunctorCall()) {
            sink.occurrence(IndexKeys.FUNCTORS_CALL_FQN/*CALL?*/, fqnHash);
        }
        if (stub.isComponent()) {
            sink.occurrence(IndexKeys.MODULES_COMP_FQN, fqnHash);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
