package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ValFqnIndex extends IntStubIndexExtension<PsiVal> {
    private static final int VERSION = 2;
    private static final ValFqnIndex INSTANCE = new ValFqnIndex();

    @NotNull
    public static ValFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<Integer, PsiVal> getKey() {
        return IndexKeys.VALS_FQN;
    }

    @NotNull
    @Override
    public Collection<PsiVal> get(@NotNull final Integer integer, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), integer, project, /*new JavaSourceFilterScope(scope) TODO*/scope, PsiVal.class);
    }
}
