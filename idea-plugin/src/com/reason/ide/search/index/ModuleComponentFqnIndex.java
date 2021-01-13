package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ModuleComponentFqnIndex extends IntStubIndexExtension<PsiModule> {

    private static final int VERSION = 1;
    private static final ModuleComponentFqnIndex INSTANCE = new ModuleComponentFqnIndex();

    public static @NotNull ModuleComponentFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiModule> getKey() {
        return IndexKeys.MODULES_COMP_FQN;
    }
}
