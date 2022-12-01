package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ExternalFqnIndex extends IntStubIndexExtension<RPsiExternal> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.EXTERNAL;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiExternal> getKey() {
        return IndexKeys.EXTERNALS_FQN;
    }

    public static @NotNull Collection<RPsiExternal> getElements(int key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.EXTERNALS_FQN, key, project, scope, RPsiExternal.class);
    }
}
