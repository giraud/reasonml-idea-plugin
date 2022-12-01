package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RecordFieldIndex extends StringStubIndexExtension<RPsiRecordField> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.RECORD_FIELD;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiRecordField> getKey() {
        return IndexKeys.RECORD_FIELDS;
    }

    public static @NotNull Collection<RPsiRecordField> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.RECORD_FIELDS, key, project, scope, RPsiRecordField.class);
    }
}
