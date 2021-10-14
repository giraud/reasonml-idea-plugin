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

public abstract class PsiModuleStubElementType extends ORStubElementType<PsiModuleStub, PsiModule> {
    public static final int VERSION = 23;

    protected PsiModuleStubElementType(@NotNull String name, Language language) {
        super(name, language);
    }

    @NotNull
    public PsiModuleStub createStub(@NotNull final PsiModule psi, final StubElement parentStub) {
        boolean isFunctorCall = false;
        if (psi instanceof PsiInnerModule) {
            isFunctorCall = ((PsiInnerModule) psi).isFunctorCall();
        }

        return new PsiModuleStub(parentStub, this, psi.getName(), psi.getPath(), psi.getQualifiedNameAsPath(), null, psi.getAlias(), psi.isComponent(), psi.isInterface(), psi instanceof PsiFakeModule, isFunctorCall);
    }

    public void serialize(@NotNull final PsiModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
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

    public @NotNull PsiModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
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

    public void indexStub(@NotNull final PsiModuleStub stub, @NotNull final IndexSink sink) {
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
        if (stub.isComponent()) {
            sink.occurrence(IndexKeys.MODULES_COMP_FQN, fqnHash);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
