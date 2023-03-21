package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class FunctorCallFqnIndex extends IntStubIndexExtension<RPsiInnerModule> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.MODULE;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiInnerModule> getKey() {
        return IndexKeys.FUNCTORS_CALL_FQN;
    }

    public static @NotNull Collection<RPsiInnerModule> getElements(@NotNull String qname, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.FUNCTORS_CALL_FQN, qname.hashCode(), project, scope, RPsiInnerModule.class);
    }
}
