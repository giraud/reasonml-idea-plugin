package com.reason.ide.search;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiExternal;
import org.jetbrains.annotations.NotNull;

public class ExternalIndex extends StringStubIndexExtension<PsiExternal> {
    private static final int VERSION = 3;

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
