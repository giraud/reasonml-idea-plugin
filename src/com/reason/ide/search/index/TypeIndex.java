package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class TypeIndex extends StringStubIndexExtension<PsiType> {
    private static final int VERSION = 9;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiType> getKey() {
        return IndexKeys.TYPES;
    }
}
