package com.reason.ide.search.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiRecordField;
import org.jetbrains.annotations.NotNull;

public class RecordFieldIndex extends StringStubIndexExtension<PsiRecordField> {
  private static final int VERSION = 3;

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<String, PsiRecordField> getKey() {
    return IndexKeys.RECORD_FIELDS;
  }
}
