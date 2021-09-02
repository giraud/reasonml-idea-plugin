package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class LetIndex extends StringStubIndexExtension<PsiLet> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiLetStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiLet> getKey() {
        return IndexKeys.LETS;
    }

    public static @NotNull Collection<PsiLet> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.LETS, key, project, scope, PsiLet.class);
    }
}
