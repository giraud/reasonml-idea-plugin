package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExceptionIndex extends StringStubIndexExtension<PsiException> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiExceptionStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiException> getKey() {
        return IndexKeys.EXCEPTIONS;
    }

    public static @NotNull Collection<PsiException> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXCEPTIONS, key, project, scope, PsiException.class);
    }
}
