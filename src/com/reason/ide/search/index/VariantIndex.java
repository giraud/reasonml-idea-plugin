package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class VariantIndex extends StringStubIndexExtension<PsiVariantDeclaration> {
    private static final int VERSION = 3;
    private static final VariantIndex INSTANCE = new VariantIndex();

    public static @NotNull VariantIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, PsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS;
    }
}
