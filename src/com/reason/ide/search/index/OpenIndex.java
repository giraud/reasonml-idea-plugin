package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OpenIndex extends StringStubIndexExtension<PsiOpen> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiOpenStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiOpen> getKey() {
        return IndexKeys.OPENS;
    }

    public static @NotNull Collection<PsiOpen> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.OPENS, key, project, scope, PsiOpen.class);
    }
}
