package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ClassMethodFqnIndex extends IntStubIndexExtension<RsiClassMethod> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.CLASS_METHOD;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RsiClassMethod> getKey() {
        return IndexKeys.CLASS_METHODS_FQN;
    }

    public static @NotNull Collection<RsiClassMethod> getElements(int key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.CLASS_METHODS_FQN, key, project, scope, RsiClassMethod.class);
    }
}
