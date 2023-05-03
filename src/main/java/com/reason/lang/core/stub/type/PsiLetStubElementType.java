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

public class PsiLetStubElementType extends ORStubElementType<PsiLetStub, RPsiLet> {
    public PsiLetStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiLetImpl createPsi(@NotNull PsiLetStub stub) {
        return new RPsiLetImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    @Override
    public @NotNull PsiElement createPsi(@NotNull ASTNode node) {
        return new RPsiLetImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull PsiLetStub createStub(@NotNull RPsiLet psi, @Nullable StubElement parentStub) {
        List<String> deconstructedNames = new ArrayList<>();
        if (psi.isDeconstruction()) {
            List<PsiElement> elements = psi.getDeconstructedElements();
            for (PsiElement element : elements) {
                if (element instanceof RPsiLowerSymbol) {
                    deconstructedNames.add(element.getText());
                }
            }
        }
        return new PsiLetStub(parentStub, this, psi.getName(), psi.getPath(), psi.getAlias(), psi.isFunction(), psi.isComponent(), deconstructedNames);
    }

    public void serialize(@NotNull PsiLetStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
        SerializerUtil.writePath(dataStream, stub.getPath());
        dataStream.writeBoolean(stub.isFunction());
        dataStream.writeBoolean(stub.isComponent());

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

    public @NotNull PsiLetStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
        StringRef name = dataStream.readName();
        String[] path = SerializerUtil.readPath(dataStream);
        boolean isFunction = dataStream.readBoolean();
        boolean isComponent = dataStream.readBoolean();

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

        return new PsiLetStub(parentStub, this, name, path, alias, isFunction, isComponent, deconstructionNames);
    }

    public void indexStub(@NotNull PsiLetStub stub, @NotNull IndexSink sink) {
        List<String> deconstructionNames = stub.getDeconstructionNames();
        if (deconstructionNames.isEmpty()) {
            // Normal let

            String fqn = stub.getQualifiedName();
            sink.occurrence(IndexKeys.LETS_FQN, fqn.hashCode());
            if (stub.isComponent()) {
                sink.occurrence(IndexKeys.LETS_COMP_FQN, fqn.hashCode());
            }
        } else {
            // Deconstruction

            for (String fqn : stub.getQualifiedNames()) {
                sink.occurrence(IndexKeys.LETS_FQN, fqn.hashCode());
            }
        }
    }

    public @NotNull String getExternalId() {
        return getLanguage().getID() + "." + super.toString();
    }
}
