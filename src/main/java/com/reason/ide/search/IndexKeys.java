package com.reason.ide.search;

import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.*;

public class IndexKeys {
    public static final StubIndexKey<Integer, PsiModule> MODULES_FQN = StubIndexKey.createIndexKey("reason.module.fqn");
    public static final StubIndexKey<String, PsiModule> MODULES = StubIndexKey.createIndexKey("reason.module");
    public static final StubIndexKey<String, PsiLet> LETS = StubIndexKey.createIndexKey("reason.let");
    public static final StubIndexKey<String, PsiVal> VALS = StubIndexKey.createIndexKey("reason.val");
    public static final StubIndexKey<String, PsiExternal> EXTERNALS = StubIndexKey.createIndexKey("reason.external");
    public static final StubIndexKey<String, PsiType> TYPES = StubIndexKey.createIndexKey("reason.type");

    private IndexKeys() {
    }
}
