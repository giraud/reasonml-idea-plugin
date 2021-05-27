package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TypeIndex extends StringStubIndexExtension<PsiType> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiTypeStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiType> getKey() {
        return IndexKeys.TYPES;
    }

    public static @NotNull Collection<PsiType> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.TYPES, key, project, scope, PsiType.class);
    }
}
