package com.reason.ide.search;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.Module;

public class ModuleIndex extends StringStubIndexExtension<Module> {
    private static final int VERSION = 1;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Module> getKey() {
        return IndexKeys.MODULES;
    }
}
