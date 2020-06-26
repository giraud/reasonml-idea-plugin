package com.reason.lang.core.stub;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import com.reason.lang.core.psi.PsiModule;

public class PsiModuleStub extends NamedStubBase<PsiModule> {
    private final String m_path;
    private final String m_qname;
    private final String m_alias;
    private final boolean m_isComponent;
    private final boolean m_isInterface;

    public PsiModuleStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable String name, @Nullable String path, String alias,
                         boolean isComponent, boolean isInterface) {
        super(parent, elementType, name);
        m_path = path;
        m_qname = path == null || path.length() == 0 ? "" + name : path + "." + name;
        m_alias = alias;
        m_isComponent = isComponent;
        m_isInterface = isInterface;
    }

    public PsiModuleStub(StubElement parent, @NotNull IStubElementType elementType, @Nullable StringRef name, @Nullable String path, String alias,
                         boolean isComponent, boolean isInterface) {
        super(parent, elementType, name);
        m_path = path;
        m_qname = path == null || path.length() == 0 ? "" + name : path + "." + name;
        m_alias = alias;
        m_isComponent = isComponent;
        m_isInterface = isInterface;
    }

    public String getPath() {
        return m_path;
    }

    @NotNull
    public String getQualifiedName() {
        return m_qname;
    }

    public String getAlias() {
        return m_alias;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isInterface() {
        return m_isInterface;
    }
}
