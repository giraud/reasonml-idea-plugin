package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiInnerModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ModuleFqnIndex extends IntStubIndexExtension<PsiInnerModule> {
    private static final int VERSION = 5;
    private static final ModuleFqnIndex INSTANCE = new ModuleFqnIndex();

    @NotNull
    public static ModuleFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<Integer, PsiInnerModule> getKey() {
        return IndexKeys.MODULES_FQN;
    }

    @NotNull
    @Override
    public Collection<PsiInnerModule> get(@NotNull final Integer integer, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), integer, project, /*new JavaSourceFilterScope(scope) TODO*/scope, PsiInnerModule.class);
    }
}
