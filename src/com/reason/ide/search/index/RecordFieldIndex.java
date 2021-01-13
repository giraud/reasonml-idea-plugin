package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class RecordFieldIndex extends StringStubIndexExtension<PsiRecordField> {
    private static final int VERSION = 3;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiRecordField> getKey() {
        return IndexKeys.RECORD_FIELDS;
    }
}
