package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExternalFqnIndex extends IntStubIndexExtension<PsiExternal> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiExternalStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiExternal> getKey() {
        return IndexKeys.EXTERNALS_FQN;
    }

    public static @NotNull Collection<PsiExternal> getElements(int key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXTERNALS_FQN, key, project, scope, PsiExternal.class);
    }
}
