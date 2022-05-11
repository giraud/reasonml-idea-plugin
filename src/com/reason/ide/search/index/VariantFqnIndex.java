package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class VariantFqnIndex extends IntStubIndexExtension<PsiVariantDeclaration> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.VARIANT;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS_FQN;
    }

    public static @NotNull Collection<PsiVariantDeclaration> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.VARIANTS_FQN, key.hashCode(), project, scope, PsiVariantDeclaration.class);
    }

    public static @Nullable PsiVariantDeclaration getElement(@Nullable String qname, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        Collection<PsiVariantDeclaration> variants = qname == null ? emptyList() : VariantFqnIndex.getElements(qname, project, scope);
        return variants.isEmpty() ? null : variants.iterator().next();
    }
}
