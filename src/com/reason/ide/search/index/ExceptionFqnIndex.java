package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ExceptionFqnIndex extends IntStubIndexExtension<PsiException> {
    private static final int VERSION = 3;
    private static final ExceptionFqnIndex INSTANCE = new ExceptionFqnIndex();

    @NotNull
    public static ExceptionFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiException> getKey() {
        return IndexKeys.EXCEPTIONS_FQN;
    }
}
