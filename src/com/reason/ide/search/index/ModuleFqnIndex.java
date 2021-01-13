package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ModuleFqnIndex extends IntStubIndexExtension<PsiModule> {
    private static final int VERSION = 8;
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
    public StubIndexKey<Integer, PsiModule> getKey() {
        return IndexKeys.MODULES_FQN;
    }
}
