package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiLet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LetFqnIndex extends IntStubIndexExtension<PsiLet> {
    private static final int VERSION = 3;
    private static final LetFqnIndex INSTANCE = new LetFqnIndex();

    @NotNull
    public static LetFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<Integer, PsiLet> getKey() {
        return IndexKeys.LETS_FQN;
    }

    @NotNull
    @Override
    public Collection<PsiLet> get(@NotNull final Integer integer, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), integer, project, /*new JavaSourceFilterScope(scope) TODO*/scope, PsiLet.class);
    }
}
