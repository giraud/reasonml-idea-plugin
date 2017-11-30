package com.reason.lang.core.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

public class ModuleIndex extends StringStubIndexExtension<PsiModule> {
    private static final ModuleIndex INSTANCE = new ModuleIndex();
    private static final int VERSION = 1;

    public static ModuleIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiModule> getKey() {
        return RmlStubIndexKeys.MODULES;
    }
}
