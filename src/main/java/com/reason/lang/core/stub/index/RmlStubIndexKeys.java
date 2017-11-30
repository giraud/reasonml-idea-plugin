package com.reason.lang.core.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;

public class RmlStubIndexKeys {
    public static final StubIndexKey<String, PsiModule> MODULES = StubIndexKey.createIndexKey("reason.module");
    public static final StubIndexKey<String, PsiLet> LETS = StubIndexKey.createIndexKey("reason.let");

    private RmlStubIndexKeys() {
    }
}
