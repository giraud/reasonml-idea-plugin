package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ValFqnIndex extends IntStubIndexExtension<PsiVal> {
    private static final int VERSION = 3;
    private static final ValFqnIndex INSTANCE = new ValFqnIndex();

    public static @NotNull ValFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiVal> getKey() {
        return IndexKeys.VALS_FQN;
    }
}
