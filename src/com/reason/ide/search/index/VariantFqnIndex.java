package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class VariantFqnIndex extends IntStubIndexExtension<PsiVariantDeclaration> {
    private static final int VERSION = 4;
    private static final VariantFqnIndex INSTANCE = new VariantFqnIndex();

    public static @NotNull VariantFqnIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @Override
    public @NotNull StubIndexKey<Integer, PsiVariantDeclaration> getKey() {
        return IndexKeys.VARIANTS_FQN;
    }
}
