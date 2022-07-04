package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExternalIndex extends StringStubIndexExtension<PsiExternal> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.EXTERNAL;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiExternal> getKey() {
        return IndexKeys.EXTERNALS;
    }

    public static @NotNull Collection<PsiExternal> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXTERNALS, key, project, scope, PsiExternal.class);
    }
}
