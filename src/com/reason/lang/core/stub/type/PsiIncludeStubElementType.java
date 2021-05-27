package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiIncludeStubElementType extends ORStubElementType<PsiIncludeStub, PsiInclude> {
    public static final int VERSION = 2;

    public PsiIncludeStubElementType(@Nullable Language language) {
        super("C_INCLUDE", language);
    }

    @Override public PsiInclude createPsi(@NotNull PsiIncludeStub stub) {
        return new PsiIncludeImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @Override public @NotNull PsiElement createPsi(ASTNode node) {
        return new PsiIncludeImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @Override public @NotNull PsiIncludeStub createStub(@NotNull PsiInclude psi, StubElement parentStub) {
        return new PsiIncludeStub(parentStub, this, ((FileBase) psi.getContainingFile()).getModuleName(), psi.getIncludePath(), psi.getQualifiedPath(), psi.getResolvedPath());
    }

    @Override public void serialize(@NotNull PsiIncludeStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getFileModule());
        dataStream.writeUTFFast(stub.getIncludePath());
        SerializerUtil.writePath(dataStream, stub.getQualifiedPath());
        SerializerUtil.writePath(dataStream, stub.getResolvedPath());
    }

    @Override public @NotNull PsiIncludeStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef fileModule = dataStream.readName();
        String includePath = dataStream.readUTFFast();
        String[] qualifiedPath = SerializerUtil.readPath(dataStream);
        String[] resolvedPath = SerializerUtil.readPath(dataStream);
        return new PsiIncludeStub(parentStub, this, fileModule, includePath, qualifiedPath, resolvedPath);
    }

    @Override public void indexStub(@NotNull PsiIncludeStub stub, @NotNull IndexSink sink) {
        sink.occurrence(IndexKeys.INCLUDES, Joiner.join(".", stub.getResolvedPath()));
    }

    @Override public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
