package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ExternalIndex extends StringStubIndexExtension<PsiExternal> {
    private static final int VERSION = 7;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiExternal> getKey() {
        return IndexKeys.EXTERNALS;
    }
}
