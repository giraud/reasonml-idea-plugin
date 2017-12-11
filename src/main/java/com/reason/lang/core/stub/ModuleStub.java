package com.reason.lang.core.stub;

import com.intellij.psi.stubs.StubElement;
import com.reason.lang.core.psi.impl.ModuleImpl;

public interface ModuleStub extends StubElement<ModuleImpl> {
    String getName();
}
