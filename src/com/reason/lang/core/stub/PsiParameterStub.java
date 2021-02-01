package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

public class PsiParameterStub extends NamedStubBase<PsiParameter> {
  private final String m_qname;

  public PsiParameterStub(
      StubElement parent, @NotNull IStubElementType elementType, String name, String qname) {
    super(parent, elementType, name);
    m_qname = qname;
  }

  public PsiParameterStub(
      StubElement parent, @NotNull IStubElementType elementType, StringRef name, String qname) {
    super(parent, elementType, name);
    m_qname = qname;
  }

  public String getQualifiedName() {
    return m_qname;
  }
}
