package com.reason.ide.search;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FileModuleData implements Comparable<FileModuleData> {
    private String m_moduleName;
    private boolean m_isInterface;

    public FileModuleData(String moduleName, boolean isInterface) {
        m_moduleName = moduleName;
        m_isInterface = isInterface;
    }

    public String getModuleName() {
        return m_moduleName;
    }

    public boolean isInterface() {
        return m_isInterface;
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
