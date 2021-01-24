package com.reason.lang.core.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.ParameterFqnIndex;
import com.reason.ide.search.index.ParameterIndex;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.impl.PsiParameterImpl;
import com.reason.lang.core.stub.PsiParameterStub;
import com.reason.lang.core.type.ORTypesUtil;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class PsiParameterStubElementType extends ORStubElementType<PsiParameterStub, PsiParameter> {

  public PsiParameterStubElementType(@NotNull String name, @NotNull Language language) {
    super(name, language);
  }

  @NotNull
  public PsiParameterImpl createPsi(@NotNull final PsiParameterStub stub) {
    return new PsiParameterImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }

  @NotNull
  public PsiParameterImpl createPsi(@NotNull ASTNode node) {
    return new PsiParameterImpl(ORTypesUtil.getInstance(getLanguage()), node);
  }

  @NotNull
  public PsiParameterStub createStub(
      @NotNull final PsiParameter psi, final StubElement parentStub) {
    return new PsiParameterStub(parentStub, this, psi.getName(), psi.getQualifiedName());
  }

  public void serialize(
      @NotNull final PsiParameterStub stub, @NotNull final StubOutputStream dataStream)
      throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeUTFFast(stub.getQualifiedName());
  }

  @NotNull
  public PsiParameterStub deserialize(
      @NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    StringRef name = dataStream.readName();
    String qname = dataStream.readUTFFast();
    return new PsiParameterStub(parentStub, this, name, qname);
  }

  public void indexStub(@NotNull final PsiParameterStub stub, @NotNull final IndexSink sink) {
    String name = stub.getName();
    if (name != null) {
      sink.occurrence(ParameterIndex.KEY, name);
    }

    String fqn = stub.getQualifiedName();
    if (fqn != null) {
      sink.occurrence(ParameterFqnIndex.KEY, fqn.hashCode());
    }
  }

  @NotNull
  public String getExternalId() {
    return getLanguage().getID() + "." + super.toString();
  }
}
