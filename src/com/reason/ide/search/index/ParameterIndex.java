package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ParameterIndex extends StringStubIndexExtension<PsiParameterDeclaration> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.PARAMETER;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiParameterDeclaration> getKey() {
        return IndexKeys.PARAMETERS;
    }

    public static @NotNull Collection<PsiParameterDeclaration> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.PARAMETERS, key, project, scope, PsiParameterDeclaration.class);
    }
}
