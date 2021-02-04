package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ParameterIndex extends StringStubIndexExtension<PsiParameter> {
    private static final int VERSION = 3;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiParameter> getKey() {
        return IndexKeys.PARAMETERS;
    }
}
