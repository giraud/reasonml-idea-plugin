package com.reason.lang.core.stub;

import com.intellij.psi.stubs.StubElement;
import com.reason.lang.core.psi.PsiLet;

public interface LetStub extends StubElement<PsiLet> {
    String getName();
}
