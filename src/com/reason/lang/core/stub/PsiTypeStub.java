package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiType;
import org.jetbrains.annotations.NotNull;

public class PsiTypeStub extends NamedStubBase<PsiType> {
  private final String m_path;
  private final @NotNull String m_qname;

  public PsiTypeStub(StubElement parent, @NotNull IStubElementType elementType, String name, String path) {
    super(parent, elementType, name);
    m_path = path;
    m_qname = path + "." + getName();
  }

  public PsiTypeStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String path) {
    super(parent, elementType, name);
    m_path = path;
    m_qname = path + "." + getName();
  }

  public String getPath() {
    return m_path;
  }

  public @NotNull String getQualifiedName() {
    return m_qname;
  }
}
