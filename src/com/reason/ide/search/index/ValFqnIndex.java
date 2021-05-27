package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ValFqnIndex extends IntStubIndexExtension<PsiVal> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiValStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiVal> getKey() {
        return IndexKeys.VALS_FQN;
    }

    public static @NotNull Collection<PsiVal> getElements(int key, @NotNull Project project) {
        return StubIndex.getElements(IndexKeys.VALS_FQN, key, project, null, PsiVal.class);
    }
}
