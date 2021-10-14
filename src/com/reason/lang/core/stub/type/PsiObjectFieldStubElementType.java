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
    public static final int VERSION = 2;

    public PsiObjectFieldStubElementType(@NotNull Language language) {
        super("C_OBJECT_FIELD", language);
    }

    @NotNull
    public PsiObjectField createPsi(@NotNull PsiObjectFieldStub stub) {
        return new PsiObjectField(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @NotNull
    public PsiObjectField createPsi(@NotNull ASTNode node) {
        return new PsiObjectField(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiObjectFieldStub createStub(@NotNull PsiObjectField psi, StubElement parentStub) {
        return new PsiObjectFieldStub(parentStub, this, psi.getName(), psi.getPath());
    }

    public void serialize(@NotNull final PsiObjectFieldStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
    }

    @NotNull
    public PsiObjectFieldStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        return new PsiObjectFieldStub(parentStub, this, name, path);
    }

    public void indexStub(@NotNull PsiObjectFieldStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        if (name != null) {
            sink.occurrence(IndexKeys.OBJECT_FIELDS, name);
        }
    }

    @NotNull
    public String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
