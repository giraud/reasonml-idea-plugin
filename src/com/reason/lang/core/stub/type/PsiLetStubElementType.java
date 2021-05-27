package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class PsiLetStubElementType extends ORStubElementType<PsiLetStub, PsiLet> {
    public static final int VERSION = 12;

    public PsiLetStubElementType(@Nullable Language language) {
        super("C_LET_DECLARATION", language);
    }

    @NotNull
    public PsiLetImpl createPsi(@NotNull PsiLetStub stub) {
        return new PsiLetImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @Override
    public @NotNull PsiElement createPsi(ASTNode node) {
        return new PsiLetImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiLetStub createStub(@NotNull PsiLet psi, StubElement parentStub) {
        List<String> deconstructedNames = new ArrayList<>();
        if (psi.isDeconstruction()) {
            List<PsiElement> elements = psi.getDeconstructedElements();
            for (PsiElement element : elements) {
                if (element instanceof PsiLowerIdentifier) {
                    deconstructedNames.add(element.getText());
                }
            }
        }
        return new PsiLetStub(parentStub, this, psi.getName(), psi.getPath(), psi.getAlias(), psi.isFunction(), deconstructedNames);
    }

    public void serialize(@NotNull PsiLetStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isFunction());

        List<String> deconstructionNames = stub.getDeconstructionNames();
        dataStream.writeByte(deconstructionNames.size());
        if (!deconstructionNames.isEmpty()) {
            for (String name : deconstructionNames) {
                dataStream.writeUTFFast(name);
            }
        }

        String alias = stub.getAlias();
        dataStream.writeBoolean(alias != null);
        if (alias != null) {
            dataStream.writeUTFFast(stub.getAlias());
        }
    }

    @NotNull
    public PsiLetStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        boolean isFunction = dataStream.readBoolean();

        List<String> deconstructionNames = new ArrayList<>();
        byte namesCount = dataStream.readByte();
        if (namesCount > 0) {
            for (int i = 0; i < namesCount; i++) {
                deconstructionNames.add(dataStream.readUTFFast());
            }
        }

        String alias = null;
        boolean isAlias = dataStream.readBoolean();
        if (isAlias) {
            alias = dataStream.readUTFFast();
        }

        return new PsiLetStub(parentStub, this, name, path, alias, isFunction, deconstructionNames);
    }

    public void indexStub(@NotNull PsiLetStub stub, @NotNull IndexSink sink) {
        List<String> deconstructionNames = stub.getDeconstructionNames();
        if (deconstructionNames.isEmpty()) {
            String name = stub.getName();
            if (name != null) {
                sink.occurrence(IndexKeys.LETS, name);
            }
        } else {
            for (String name : deconstructionNames) {
                sink.occurrence(IndexKeys.LETS, name);
            }
        }

        String fqn = stub.getQualifiedName();
        if (fqn != null) {
            sink.occurrence(IndexKeys.LETS_FQN, fqn.hashCode());
        }
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
