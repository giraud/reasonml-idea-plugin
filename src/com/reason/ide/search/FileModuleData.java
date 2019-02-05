package com.reason.ide.search;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FileModuleData implements Comparable<FileModuleData> {
    private final String m_namespace;
    private final String m_moduleName;
    private final boolean m_isInterface;
    private final boolean m_isComponent;

    FileModuleData(String namespace, String moduleName, boolean isInterface, boolean hasInterface) {
        m_namespace = namespace;
        m_moduleName = moduleName;
        m_isInterface = isInterface;
        m_isComponent = hasInterface;
    }

    public String getNamespace() {
        return m_namespace;
    }

    String getModuleName() {
        return m_moduleName;
    }

    boolean isInterface() {
        return m_isInterface;
    }

    boolean isComponent() {
        return m_isComponent;
    }

    @Override
    public int compareTo(@NotNull FileModuleData o) {
        int comp = Comparing.compare(m_namespace, o.m_namespace);
        if (comp == 0) {
            comp = Comparing.compare(m_moduleName, o.m_moduleName);
            if (comp == 0) {
                comp = Comparing.compare(m_isInterface, o.m_isInterface);
            }
        }
        return comp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileModuleData that = (FileModuleData) o;
        return m_isInterface == that.m_isInterface &&
                Objects.equals(m_namespace, that.m_namespace) &&
                Objects.equals(m_moduleName, that.m_moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_namespace, m_moduleName, m_isInterface);
    }

    @Override
    public String toString() {
        return "FileModuleData{" +
                "namespace='" + m_namespace + '\'' +
                ", moduleName='" + m_moduleName + '\'' +
                ", isInterface=" + m_isInterface +
                ", isComponent=" + m_isComponent +
                '}';
    }
}
