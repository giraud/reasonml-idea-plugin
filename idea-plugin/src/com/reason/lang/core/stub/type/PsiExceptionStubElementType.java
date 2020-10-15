package com.reason.lang.core.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.impl.PsiExceptionImpl;
import com.reason.lang.core.stub.PsiExceptionStub;
import com.reason.lang.core.type.ORTypesUtil;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class PsiExceptionStubElementType extends ORStubElementType<PsiExceptionStub, PsiException> {

  public PsiExceptionStubElementType(Language language) {
    super("C_EXCEPTION_DECLARATION", language);
  }

  @NotNull
  public PsiException createPsi(@NotNull PsiExceptionStub stub) {
    return new PsiExceptionImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }

  @NotNull
  public PsiException createPsi(@NotNull ASTNode node) {
    return new PsiExceptionImpl(ORTypesUtil.getInstance(getLanguage()), node);
  }

  @NotNull
  public PsiExceptionStub createStub(@NotNull final PsiException psi, StubElement parentStub) {
    return new PsiExceptionStub(parentStub, this, psi.getName(), psi.getPath());
  }

  public void serialize(
      @NotNull final PsiExceptionStub stub, @NotNull final StubOutputStream dataStream)
      throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeUTFFast(stub.getPath());
  }

  @NotNull
  public PsiExceptionStub deserialize(
      @NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    StringRef name = dataStream.readName();
    String path = dataStream.readUTFFast();

    return new PsiExceptionStub(parentStub, this, name, path);
  }

  public void indexStub(@NotNull final PsiExceptionStub stub, @NotNull final IndexSink sink) {
    String name = stub.getName();
    if (name != null) {
      sink.occurrence(IndexKeys.EXCEPTIONS, name);
    }

    String fqn = stub.getQualifiedName();
    sink.occurrence(IndexKeys.EXCEPTIONS_FQN, fqn.hashCode());
  }

  @NotNull
  public String getExternalId() {
    return getLanguage().getID() + "." + super.toString();
  }
}
