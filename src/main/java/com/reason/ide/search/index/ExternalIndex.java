package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExternalIndex extends StringStubIndexExtension<RPsiExternal> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.EXTERNAL;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiExternal> getKey() {
        return IndexKeys.EXTERNALS;
    }

    public static @NotNull Collection<RPsiExternal> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXTERNALS, key, project, scope, RPsiExternal.class);
    }
}
