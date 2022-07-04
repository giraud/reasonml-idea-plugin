package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TypeFqnIndex extends IntStubIndexExtension<PsiType> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.TYPE;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiType> getKey() {
        return IndexKeys.TYPES_FQN;
    }

    public static @NotNull Collection<PsiType> getElements(int key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.TYPES_FQN, key, project, scope, PsiType.class);
    }
}
