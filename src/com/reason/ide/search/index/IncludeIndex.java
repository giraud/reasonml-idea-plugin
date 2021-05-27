package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class IncludeIndex extends StringStubIndexExtension<PsiInclude> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiIncludeStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiInclude> getKey() {
        return IndexKeys.INCLUDES;
    }

    public static @NotNull Collection<PsiInclude> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.INCLUDES, key, project, scope, PsiInclude.class);
    }
}
