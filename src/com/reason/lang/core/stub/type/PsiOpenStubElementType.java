package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiOpenStubElementType extends ORStubElementType<PsiOpenStub, PsiOpen> {
    public static final int VERSION = 1;

    public PsiOpenStubElementType(@Nullable Language language) {
        super("C_OPEN", language);
    }

    @Override
    public @NotNull PsiOpen createPsi(@NotNull PsiOpenStub stub) {
        return new PsiOpenImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @Override
    public @NotNull PsiElement createPsi(@NotNull ASTNode node) {
        return new PsiOpenImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @Override
    public @NotNull PsiOpenStub createStub(@NotNull PsiOpen psi, StubElement parentStub) {
        return new PsiOpenStub(parentStub, this, psi.getPath());
    }

    @Override
    public void serialize(@NotNull PsiOpenStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeUTFFast(stub.getOpenPath());
    }

    @Override
    public @NotNull PsiOpenStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String openPath = dataStream.readUTFFast();
        return new PsiOpenStub(parentStub, this, openPath);
    }

    @Override
    public void indexStub(@NotNull PsiOpenStub stub, @NotNull IndexSink sink) {
        sink.occurrence(IndexKeys.OPENS, stub.getOpenPath());
    }

    @Override
    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
