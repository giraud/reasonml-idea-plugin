package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.stub.PsiVariantDeclarationStub;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTypesUtil;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class PsiVariantStubElementType
    extends IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration>
    implements ORCompositeType {

  public PsiVariantStubElementType(@NotNull String name, Language language) {
    super(name, language);
  }

  @NotNull
  public PsiVariantDeclaration createPsi(@NotNull final PsiVariantDeclarationStub stub) {
    return new PsiVariantDeclaration(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }

  @NotNull
  public PsiVariantDeclarationStub createStub(
      @NotNull final PsiVariantDeclaration psi, final StubElement parentStub) {
    return new PsiVariantDeclarationStub(parentStub, this, psi.getName(), psi.getPath());
  }

  public void serialize(
      @NotNull final PsiVariantDeclarationStub stub, @NotNull final StubOutputStream dataStream)
      throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeUTFFast(stub.getPath());
  }

  @NotNull
  public PsiVariantDeclarationStub deserialize(
      @NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    StringRef moduleName = dataStream.readName();
    String path = dataStream.readUTFFast();

    return new PsiVariantDeclarationStub(parentStub, this, moduleName, path);
  }

  public void indexStub(
      @NotNull final PsiVariantDeclarationStub stub, @NotNull final IndexSink sink) {
    String name = stub.getName();
    if (name != null) {
      sink.occurrence(IndexKeys.VARIANTS, name);
    }

    String fqn = stub.getQualifiedName();
    if (fqn != null) {
      sink.occurrence(IndexKeys.VARIANTS_FQN, fqn.hashCode());
    }
  }

  @NotNull
  public String getExternalId() {
    return getLanguage() + "." + super.toString();
  }
}
