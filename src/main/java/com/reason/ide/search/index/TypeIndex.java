package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TypeIndex extends StringStubIndexExtension<RPsiType> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.TYPE;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiType> getKey() {
        return IndexKeys.TYPES;
    }

    public static @NotNull Collection<RPsiType> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.TYPES, key, project, scope, RPsiType.class);
    }
}
