package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleComponentFqnIndex extends IntStubIndexExtension<PsiModule> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiModuleStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiModule> getKey() {
        return IndexKeys.MODULES_COMP_FQN;
    }

    public static @NotNull Collection<PsiModule> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.MODULES_COMP_FQN, key.hashCode(), project, scope, PsiModule.class);
    }
}
