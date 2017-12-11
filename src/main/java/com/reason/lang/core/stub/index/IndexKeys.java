package com.reason.lang.core.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.Module;
import com.reason.lang.core.psi.PsiLet;

public class IndexKeys {
    public static final StubIndexKey<String, Module> MODULES = StubIndexKey.createIndexKey("reason.module");
    public static final StubIndexKey<String, PsiLet> LETS = StubIndexKey.createIndexKey("reason.let");

    private IndexKeys() {
    }
}
