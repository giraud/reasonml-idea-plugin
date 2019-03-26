package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;

public class PsiValStub extends NamedStubBase<PsiVal> {
    private String m_qname;

    public PsiValStub(StubElement parent, @NotNull IStubElementType elementType, String name, String qname) {
        super(parent, elementType, name);
        m_qname = qname;
    }

    public PsiValStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String qname) {
        super(parent, elementType, name);
        m_qname = qname;
    }

    public String getQualifiedName() {
        return m_qname;
    }
}