package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;

public class PsiValStub extends NamedStubBase<PsiVal> {
  private final String m_path;
  private final @NotNull String m_qname;
  private final boolean m_isFunction;

  public PsiValStub(StubElement parent, @NotNull IStubElementType elementType, String name, String path, boolean isFunction) {
    super(parent, elementType, name);
    m_path = path;
    m_qname = path + "." + name;
    m_isFunction = isFunction;
  }

  public PsiValStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String path, boolean isFunction) {
    super(parent, elementType, name);
    m_path = path;
    m_qname = path + "." + name;
    m_isFunction = isFunction;
  }

  public String getPath() {
    return m_path;
  }

  public @NotNull String getQualifiedName() {
    return m_qname;
  }

  public boolean isFunction() {
    return m_isFunction;
  }
}
