package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class VariantFqnIndex extends IntStubIndexExtension<RPsiVariantDeclaration> {
    @Override
    public int getVersion() {
        return super.getVersion() + ORStubVersions.VARIANT;
    }

    @Override
    public @NotNull StubIndexKey<Integer, RPsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS_FQN;
    }

    public static @NotNull Collection<RPsiVariantDeclaration> getElements(@NotNull String key, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        return StubIndex.getElements(IndexKeys.VARIANTS_FQN, key.hashCode(), project, scope, RPsiVariantDeclaration.class);
    }

    public static @Nullable RPsiVariantDeclaration getElement(@Nullable String qname, @NotNull Project project, @Nullable GlobalSearchScope scope) {
        Collection<RPsiVariantDeclaration> variants = qname == null ? emptyList() : VariantFqnIndex.getElements(qname, project, scope);
        return variants.isEmpty() ? null : variants.iterator().next();
    }
}
