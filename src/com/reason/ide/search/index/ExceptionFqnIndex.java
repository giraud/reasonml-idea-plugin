package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExceptionFqnIndex extends IntStubIndexExtension<PsiException> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.EXCEPTION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiException> getKey() {
        return IndexKeys.EXCEPTIONS_FQN;
    }

    public static @NotNull Collection<PsiException> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXCEPTIONS_FQN, key.hashCode(), project, scope, PsiException.class);
    }
}
