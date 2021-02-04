package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class TypeFqnIndex extends IntStubIndexExtension<PsiType> {
    private static final int VERSION = 1;
    private static final TypeFqnIndex INSTANCE = new TypeFqnIndex();

    public static @NotNull TypeFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiType> getKey() {
        return IndexKeys.TYPES_FQN;
    }
}
