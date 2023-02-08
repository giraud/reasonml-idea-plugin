package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiIncludeStubElementType extends ORStubElementType<PsiIncludeStub, RPsiInclude> {
    public PsiIncludeStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    @Override
    public @NotNull RPsiInclude createPsi(@NotNull PsiIncludeStub stub) {
        return new RPsiIncludeImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @Override
    public @NotNull PsiElement createPsi(@NotNull ASTNode node) {
        return new RPsiIncludeImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @Override
    public @NotNull PsiIncludeStub createStub(@NotNull RPsiInclude psi, @Nullable StubElement parentStub) {
        return new PsiIncludeStub(parentStub, this, ((FileBase) psi.getContainingFile()).getModuleName(), psi.getIncludePath(), psi.getQualifiedPath());
    }

    @Override
    public void serialize(@NotNull PsiIncludeStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getFileModule());
        dataStream.writeUTFFast(stub.getIncludePath());
        SerializerUtil.writePath(dataStream, stub.getQualifiedPath());
    }

    @Override
    public @NotNull PsiIncludeStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef fileModule = dataStream.readName();
        String includePath = dataStream.readUTFFast();
        String[] qualifiedPath = SerializerUtil.readPath(dataStream);
        return new PsiIncludeStub(parentStub, this, fileModule, includePath, qualifiedPath);
    }

    @Override
    public void indexStub(@NotNull PsiIncludeStub stub, @NotNull IndexSink sink) {
        sink.occurrence(IndexKeys.INCLUDES, stub.getIncludePath());
    }

    @Override
    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
