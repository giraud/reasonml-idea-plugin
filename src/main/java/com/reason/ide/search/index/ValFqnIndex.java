package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ValFqnIndex extends IntStubIndexExtension<RPsiVal> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.VAL;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiVal> getKey() {
        return IndexKeys.VALS_FQN;
    }

    public static @NotNull Collection<RPsiVal> getElements(String qName, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.VALS_FQN, qName.hashCode(), project, scope, RPsiVal.class);
    }
}
