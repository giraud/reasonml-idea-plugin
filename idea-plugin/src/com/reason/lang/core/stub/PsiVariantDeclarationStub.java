package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import org.jetbrains.annotations.NotNull;

public class PsiVariantDeclarationStub extends NamedStubBase<PsiVariantDeclaration> {
  private final String m_path;
  private final String m_qname;

  public PsiVariantDeclarationStub(
      StubElement parent, @NotNull IStubElementType elementType, String name, String path) {
    super(parent, elementType, name);
    m_path = path;
    m_qname = path + "." + name;
  }

  public PsiVariantDeclarationStub(
      StubElement parent, @NotNull IStubElementType elementType, StringRef name, String path) {
    super(parent, elementType, name);
    m_path = path;
    m_qname = path + "." + name;
  }

  public String getPath() {
    return m_path;
  }

  public String getQualifiedName() {
    return m_qname;
  }
}
