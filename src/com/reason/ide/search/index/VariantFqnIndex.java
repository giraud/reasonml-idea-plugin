package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class VariantFqnIndex extends IntStubIndexExtension<PsiVariantDeclaration> {
    private static final int VERSION = 1;
    private static final VariantFqnIndex INSTANCE = new VariantFqnIndex();

    @NotNull
    public static VariantFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<Integer, PsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS_FQN;
    }

    @NotNull
    @Override
    public Collection<PsiVariantDeclaration> get(@NotNull final Integer integer, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), integer, project, scope, PsiVariantDeclaration.class);
    }
}
