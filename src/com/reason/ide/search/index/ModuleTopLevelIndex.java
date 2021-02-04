package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public class ModuleTopLevelIndex extends StringStubIndexExtension<PsiFakeModule> {
    private static final int VERSION = 2;
    private static final ModuleTopLevelIndex INSTANCE = new ModuleTopLevelIndex();

    @NotNull
    public static ModuleTopLevelIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiFakeModule> getKey() {
        return IndexKeys.MODULES_TOP_LEVEL;
    }
}
