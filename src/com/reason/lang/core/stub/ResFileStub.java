package com.reason.lang.core.stub;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.stub.type.ResFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class ResFileStub extends PsiFileStubImpl<FileBase> {

  private final boolean m_isComponent;

  public ResFileStub(FileBase file, boolean isComponent) {
    super(file);
    m_isComponent = isComponent;
  }

  @NotNull
  @Override
  public IStubFileElementType<ResFileStub> getType() {
    return ResFileStubElementType.INSTANCE;
  }

  public boolean isComponent() {
    return m_isComponent;
  }
}
