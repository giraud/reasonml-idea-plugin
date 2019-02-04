package com.reason.ide.search;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FileModuleData implements Comparable<FileModuleData> {
    private final String m_moduleName;
    private final boolean m_isInterface;
    private final boolean m_isComponent;

    public FileModuleData(String moduleName, boolean isInterface, boolean hasInterface) {
        m_moduleName = moduleName;
        m_isInterface = isInterface;
        m_isComponent = hasInterface;
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
        return Comparing.compare(m_moduleName, o.m_moduleName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileModuleData that = (FileModuleData) o;
        return m_isInterface == that.m_isInterface &&
                Objects.equals(m_moduleName, that.m_moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_moduleName, m_isInterface);
    }
}
