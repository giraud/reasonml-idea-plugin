package com.reason.ide.search.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiType;
import org.jetbrains.annotations.NotNull;

public class TypeIndex extends StringStubIndexExtension<PsiType> {
    private static final int VERSION = 9;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiType> getKey() {
        return IndexKeys.TYPES;
    }
}
