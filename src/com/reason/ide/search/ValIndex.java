package com.reason.ide.search;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;

public class ValIndex extends StringStubIndexExtension<PsiVal> {
    private static final int VERSION = 3;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiVal> getKey() {
        return IndexKeys.VALS;
    }
}
