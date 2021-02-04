package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class LetFqnIndex extends IntStubIndexExtension<PsiLet> {
    private static final int VERSION = 4;
    private static final LetFqnIndex INSTANCE = new LetFqnIndex();

    public static @NotNull LetFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiLet> getKey() {
        return IndexKeys.LETS_FQN;
    }
}
