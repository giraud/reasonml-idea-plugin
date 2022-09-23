package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExceptionIndex extends StringStubIndexExtension<RPsiException> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.EXCEPTION;
    }

    @Override
    public @NotNull StubIndexKey<String, RPsiException> getKey() {
        return IndexKeys.EXCEPTIONS;
    }

    public static @NotNull Collection<RPsiException> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXCEPTIONS, key, project, scope, RPsiException.class);
    }
}
