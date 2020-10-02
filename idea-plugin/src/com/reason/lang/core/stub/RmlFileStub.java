package com.reason.lang.core.stub;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.stub.type.RmlFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class RmlFileStub extends PsiFileStubImpl<FileBase> {

  private final boolean m_isComponent;

  public RmlFileStub(FileBase file, boolean isComponent) {
    super(file);
    m_isComponent = isComponent;
  }

  @NotNull
  @Override
  public IStubFileElementType<RmlFileStub> getType() {
    return RmlFileStubElementType.INSTANCE;
  }

  public boolean isComponent() {
    return m_isComponent;
  }
}
