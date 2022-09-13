package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class LetFqnIndex extends IntStubIndexExtension<PsiLet> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.LET;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiLet> getKey() {
        return IndexKeys.LETS_FQN;
    }

    public static @NotNull Collection<PsiLet> getElements(int key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.LETS_FQN, key, project, scope, PsiLet.class);
    }
}
