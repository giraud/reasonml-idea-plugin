package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class KlassFqnIndex extends IntStubIndexExtension<PsiKlass> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.KLASS;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiKlass> getKey() {
        return IndexKeys.CLASSES_FQN;
    }

    public static @NotNull Collection<PsiKlass> getElements(int key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.CLASSES_FQN, key, project, scope, PsiKlass.class);
    }
}
