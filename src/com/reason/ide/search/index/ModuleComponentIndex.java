package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiInnerModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ModuleComponentIndex extends StringStubIndexExtension<PsiInnerModule> {
    private static final int VERSION = 5;
    private static final ModuleComponentIndex INSTANCE = new ModuleComponentIndex();

    @NotNull
    public static ModuleComponentIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiInnerModule> getKey() {
        return IndexKeys.MODULES_COMP;
    }

    @Nullable
    public PsiInnerModule getUnique(@NotNull String fqn, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        Collection<PsiInnerModule> psiModules = get(fqn, project, scope);
        return psiModules.isEmpty() ? null : psiModules.iterator().next();
    }
}
