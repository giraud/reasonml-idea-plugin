package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TypeFqnIndex extends IntStubIndexExtension<RPsiType> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.TYPE;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiType> getKey() {
        return IndexKeys.TYPES_FQN;
    }

    public static @NotNull Collection<RPsiType> getElements(String qName, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.TYPES_FQN, qName.hashCode(), project, scope, RPsiType.class);
    }
}
