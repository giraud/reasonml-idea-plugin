package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ValIndex extends StringStubIndexExtension<RPsiVal> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.VAL;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiVal> getKey() {
        return IndexKeys.VALS;
    }

    public static @NotNull Collection<RPsiVal> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.VALS, key, project, scope, RPsiVal.class);
    }
}
