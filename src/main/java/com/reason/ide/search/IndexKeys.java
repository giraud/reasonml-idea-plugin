package com.reason.ide.search;

import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiLet;

public class IndexKeys {
    public static final StubIndexKey<String, PsiModule> MODULES = StubIndexKey.createIndexKey("reason.modules");
    public static final StubIndexKey<String, PsiLet> LETS = StubIndexKey.createIndexKey("reason.lets");

    private IndexKeys() {
    }
}
