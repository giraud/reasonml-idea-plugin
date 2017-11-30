package com.reason.lang.core.stub;

import com.intellij.psi.stubs.StubElement;
import com.reason.lang.core.psi.PsiModule;

public interface ModuleStub extends StubElement<PsiModule> {
    String getName();
}
