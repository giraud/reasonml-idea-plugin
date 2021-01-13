package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ValIndex extends StringStubIndexExtension<PsiVal> {
    private static final int VERSION = 10;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiVal> getKey() {
        return IndexKeys.VALS;
    }
}
