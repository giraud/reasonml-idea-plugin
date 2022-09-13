package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class PsiObjectFieldStubElementType extends ORStubElementType<PsiObjectFieldStub, PsiObjectField> {
    public PsiObjectFieldStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    public @NotNull PsiObjectField createPsi(@NotNull PsiObjectFieldStub stub) {
        return new PsiObjectField(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull PsiObjectField createPsi(@NotNull ASTNode node) {
        return new PsiObjectField(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull PsiObjectFieldStub createStub(@NotNull PsiObjectField psi, @Nullable StubElement parentStub) {
        return new PsiObjectFieldStub(parentStub, this, psi.getName(), psi.getPath());
    }

    public void serialize(@NotNull final PsiObjectFieldStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    public @NotNull PsiObjectFieldStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        return new PsiObjectFieldStub(parentStub, this, name, path == null ? EMPTY_PATH : path);
    }

    public void indexStub(@NotNull PsiObjectFieldStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.OBJECT_FIELDS, name);
        }
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
