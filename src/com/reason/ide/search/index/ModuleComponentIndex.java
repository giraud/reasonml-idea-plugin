package com.reason.ide.search.index;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiModule;

public class ModuleComponentIndex extends StringStubIndexExtension<PsiModule> {

    private static final int VERSION = 5;
    private static final ModuleComponentIndex INSTANCE = new ModuleComponentIndex();

    @NotNull
    public static ModuleComponentIndex getInstance() {
        return INSTANCE;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, PsiModule> getKey() {
        return IndexKeys.MODULES_COMP;
    }
}
