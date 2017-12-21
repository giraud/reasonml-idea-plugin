package com.reason.lang.core.stub;

import com.reason.lang.core.ModulePath;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiModule;

public class ModuleStub extends NamedStubBase<PsiModule> {
    private ModulePath m_modulePath;

    public ModuleStub(StubElement parent, @NotNull IStubElementType elementType, String name, ModulePath modulePath) {
        super(parent, elementType, name);
        m_modulePath = modulePath;
    }

    public ModuleStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, ModulePath modulePath) {
        super(parent, elementType, name);
        m_modulePath = modulePath;
    }

    public ModulePath getModulePath() {
        return m_modulePath;
    }
}
