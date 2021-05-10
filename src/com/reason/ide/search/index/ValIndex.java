package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ValIndex extends StringStubIndexExtension<PsiVal> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiValStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiVal> getKey() {
        return IndexKeys.VALS;
    }

    public static @NotNull Collection<PsiVal> getElements(String key, Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.VALS, key, project, scope, PsiVal.class);
    }
}
