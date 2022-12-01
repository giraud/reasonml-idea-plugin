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

public class PsiOpenStubElementType extends ORStubElementType<PsiOpenStub, RPsiOpen> {
    public PsiOpenStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    @Override
    public @NotNull RPsiOpen createPsi(@NotNull PsiOpenStub stub) {
        return new RPsiOpenImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @Override
    public @NotNull PsiElement createPsi(@NotNull ASTNode node) {
        return new RPsiOpenImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @Override
    public @NotNull PsiOpenStub createStub(@NotNull RPsiOpen psi, @Nullable StubElement parentStub) {
        return new PsiOpenStub(parentStub, this, psi.getPath());
    }

    @Override
    public void serialize(@NotNull PsiOpenStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String openPath = stub.getOpenPath();
        dataStream.writeUTFFast(openPath == null ? "" : openPath);
    }

    @Override
    public @NotNull PsiOpenStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        String openPath = dataStream.readUTFFast();
        return new PsiOpenStub(parentStub, this, openPath);
    }

    @Override
    public void indexStub(@NotNull PsiOpenStub stub, @NotNull IndexSink sink) {
        String openPath = stub.getOpenPath();
        if (openPath != null) {
            sink.occurrence(IndexKeys.OPENS, openPath);
        }
    }

    @Override
    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
