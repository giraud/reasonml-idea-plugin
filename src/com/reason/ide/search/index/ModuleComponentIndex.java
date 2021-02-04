package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ModuleComponentIndex extends StringStubIndexExtension<PsiModule> {

    private static final int VERSION = 8;
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
    public StubIndexKey<String, PsiModule> getKey() {
        return IndexKeys.MODULES_COMP;
    }
}
