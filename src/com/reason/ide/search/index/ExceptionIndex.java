package com.reason.ide.search.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiException;
import org.jetbrains.annotations.NotNull;

public class ExceptionIndex extends StringStubIndexExtension<PsiException> {
    private static final int VERSION = 2;
    private static final ExceptionIndex INSTANCE = new ExceptionIndex();

    @NotNull
    public static ExceptionIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiException> getKey() {
        return IndexKeys.EXCEPTIONS;
    }
}
