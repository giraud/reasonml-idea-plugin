package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class IncludeIndex extends StringStubIndexExtension<RPsiInclude> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.INCLUDE;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiInclude> getKey() {
        return IndexKeys.INCLUDES;
    }

    public static @NotNull Collection<RPsiInclude> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.INCLUDES, key, project, scope, RPsiInclude.class);
    }
}
