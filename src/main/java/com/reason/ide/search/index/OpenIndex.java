package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OpenIndex extends StringStubIndexExtension<RPsiOpen> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.OPEN;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiOpen> getKey() {
        return IndexKeys.OPENS;
    }

    public static @NotNull Collection<RPsiOpen> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.OPENS, key, project, scope, RPsiOpen.class);
    }
}
