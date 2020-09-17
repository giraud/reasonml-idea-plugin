package com.reason.ide.search.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiLet;
import org.jetbrains.annotations.NotNull;

public class LetIndex extends StringStubIndexExtension<PsiLet> {
    private static final int VERSION = 10;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiLet> getKey() {
        return IndexKeys.LETS;
    }
}
