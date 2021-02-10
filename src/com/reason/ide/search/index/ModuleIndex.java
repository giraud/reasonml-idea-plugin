package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ModuleIndex extends StringStubIndexExtension<PsiModule> {
    private static final int VERSION = 17;
    private static final ModuleIndex INSTANCE = new ModuleIndex();

    @NotNull
    public static ModuleIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiModule> getKey() {
        return IndexKeys.MODULES;
    }
}
