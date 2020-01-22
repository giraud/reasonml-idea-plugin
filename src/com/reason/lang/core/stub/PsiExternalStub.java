package com.reason.lang.core.stub;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiExternal;

public class PsiExternalStub extends NamedStubBase<PsiExternal> {
    private final String m_qname;
    private final boolean m_isFunction;

    public PsiExternalStub(StubElement parent, @NotNull IStubElementType elementType, String name, String qname, boolean isFunction) {
        super(parent, elementType, name);
        m_qname = qname;
        m_isFunction = isFunction;
    }

    public PsiExternalStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String qname, boolean isFunction) {
        super(parent, elementType, name);
        m_qname = qname;
        m_isFunction = isFunction;
    }

    public String getQualifiedName() {
        return m_qname;
    }

    public boolean isFunction() {
        return m_isFunction;
    }
}