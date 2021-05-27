package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class VariantFqnIndex extends IntStubIndexExtension<PsiVariantDeclaration> {
    @Override
    public int getVersion() {
        return super.getVersion() + PsiVariantStubElementType.VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS_FQN;
    }

    public static @NotNull Collection<PsiVariantDeclaration> getElements(String key, Project project) {
        return StubIndex.getElements(IndexKeys.VARIANTS_FQN, key.hashCode(), project, null, PsiVariantDeclaration.class);
    }

    public static PsiVariantDeclaration getElement(@Nullable String qname, @NotNull Project m_project) {
        Collection<PsiVariantDeclaration> variants = qname == null ? emptyList() : VariantFqnIndex.getElements(qname, m_project);
        return variants.isEmpty() ? null : variants.iterator().next();
    }
}
