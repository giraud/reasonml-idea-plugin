package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ParameterFqnIndex extends IntStubIndexExtension<RPsiParameterDeclaration> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.PARAMETER;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiParameterDeclaration> getKey() {
        return IndexKeys.PARAMETERS_FQN;
    }

    public static @NotNull Collection<RPsiParameterDeclaration> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.PARAMETERS_FQN, key.hashCode(), project, scope, RPsiParameterDeclaration.class);
    }
}
