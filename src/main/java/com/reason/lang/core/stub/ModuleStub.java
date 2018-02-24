package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

public class ModuleStub extends NamedStubBase<PsiModule> {
    private final String m_qname;
    private final boolean m_isFileModule;
    private final boolean m_isComponent;

    public ModuleStub(StubElement parent, @NotNull IStubElementType elementType, String name, String qname, boolean isFileModule, boolean isComponent) {
        super(parent, elementType, name);
        m_qname = qname;
        m_isFileModule = isFileModule;
        m_isComponent = isComponent;
    }

    public ModuleStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String qname, boolean isFileModule, boolean isComponent) {
        super(parent, elementType, name);
        m_qname = qname;
        m_isFileModule = isFileModule;
        m_isComponent = isComponent;
    }

    public String getQualifiedName() {
        return m_qname;
    }

    public boolean isFileModule() {
        return m_isFileModule;
    }

    public boolean isComponent() { return m_isComponent; }
}
