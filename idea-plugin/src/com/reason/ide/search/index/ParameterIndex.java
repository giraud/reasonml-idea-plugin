package com.reason.ide.search.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

public class ParameterIndex extends StringStubIndexExtension<PsiParameter> {
  private static final int VERSION = 2;

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<String, PsiParameter> getKey() {
    return IndexKeys.PARAMETERS;
  }
}
