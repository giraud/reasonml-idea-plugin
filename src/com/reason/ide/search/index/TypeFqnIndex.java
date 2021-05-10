package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TypeFqnIndex extends IntStubIndexExtension<PsiType> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiTypeStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiType> getKey() {
        return IndexKeys.TYPES_FQN;
    }

    public static @NotNull Collection<PsiType> getElements(int key, Project project) {
        return StubIndex.getElements(IndexKeys.TYPES_FQN, key, project, null, PsiType.class);
    }
}
