package com.reason.lang.core.stub;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiVariantDeclaration;

public class PsiVariantDeclarationStub extends NamedStubBase<PsiVariantDeclaration> {

    private final String m_qname;

    public PsiVariantDeclarationStub(StubElement parent, @NotNull IStubElementType elementType, String name, String qname) {
        super(parent, elementType, name);
        m_qname = qname;
    }

    public PsiVariantDeclarationStub(StubElement parent, @NotNull IStubElementType elementType, StringRef name, String qname) {
        super(parent, elementType, name);
        m_qname = qname;
    }

    public String getQualifiedName() {
        return m_qname;
    }
}
