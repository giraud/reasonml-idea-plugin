package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.impl.PsiExternalImpl;
import com.reason.lang.core.stub.PsiExternalStub;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTypesUtil;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiExternalStubElementType extends IStubElementType<PsiExternalStub, PsiExternal>
    implements ORCompositeType {

  public PsiExternalStubElementType(@NotNull String name, @Nullable Language language) {
    super(name, language);
  }

  @NotNull
  public PsiExternalImpl createPsi(@NotNull PsiExternalStub stub) {
    return new PsiExternalImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }

  @NotNull
  public PsiExternalStub createStub(@NotNull PsiExternal psi, StubElement parentStub) {
    return new PsiExternalStub(parentStub, this, psi.getName(), psi.getPath(), psi.isFunction());
  }

  public void serialize(@NotNull PsiExternalStub stub, @NotNull StubOutputStream dataStream)
      throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeUTFFast(stub.getPath());
    dataStream.writeBoolean(stub.isFunction());
  }

  @NotNull
  public PsiExternalStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub)
      throws IOException {
    StringRef name = dataStream.readName();
    String path = dataStream.readUTFFast();
    boolean isFunction = dataStream.readBoolean();

    return new PsiExternalStub(parentStub, this, name, path, isFunction);
  }

  public void indexStub(@NotNull PsiExternalStub stub, @NotNull IndexSink sink) {
    String name = stub.getName();
    if (name != null) {
      sink.occurrence(IndexKeys.EXTERNALS, name);
    }
  }

  @NotNull
  public String getExternalId() {
    return getLanguage() + "." + super.toString();
  }
}
