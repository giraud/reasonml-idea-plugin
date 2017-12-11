package com.reason.lang.core.stub.index;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.psi.PsiLet;

public class LetIndex extends StringStubIndexExtension<PsiLet> {
    private static final int VERSION = 1;

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
