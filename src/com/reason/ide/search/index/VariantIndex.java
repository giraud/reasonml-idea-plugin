package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class VariantIndex extends StringStubIndexExtension<PsiVariantDeclaration> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiVariantStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS;
    }

    public static @NotNull Collection<PsiVariantDeclaration> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.VARIANTS, key, project, scope, PsiVariantDeclaration.class);
    }
}
