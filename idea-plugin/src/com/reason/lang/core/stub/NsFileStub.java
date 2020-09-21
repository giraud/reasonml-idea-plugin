package com.reason.lang.core.stub;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.stub.type.NsFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class NsFileStub extends PsiFileStubImpl<FileBase> {

  private final boolean m_isComponent;

  public NsFileStub(FileBase file, boolean isComponent) {
    super(file);
    m_isComponent = isComponent;
  }

  @NotNull
  @Override
  public IStubFileElementType<NsFileStub> getType() {
    return NsFileStubElementType.INSTANCE;
  }

  public boolean isComponent() {
    return m_isComponent;
  }
}
