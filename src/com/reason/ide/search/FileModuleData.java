package com.reason.ide.search;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FileModuleData implements Comparable<FileModuleData>, IndexedFileModule {
    private final String m_namespace;
    private final String m_moduleName;
    private final String m_path;
    private final boolean m_isOCaml;
    private final boolean m_isInterface;
    private final boolean m_isComponent;
    private VirtualFile m_virtualFile;

    public FileModuleData(String path, String namespace, String moduleName, boolean isOCaml, boolean isInterface, boolean hasInterface) {
        m_path = path;
        m_namespace = namespace;
        m_moduleName = moduleName;
        m_isOCaml = isOCaml;
        m_isInterface = isInterface;
        m_isComponent = hasInterface;
    }

    @Override
    public String getNamespace() {
        return m_namespace;
    }

    @Override
    public String getModuleName() {
        return m_moduleName;
    }

    @Override
    public boolean isOCaml() {
        return m_isOCaml;
    }

    @Override
    public boolean isInterface() {
        return m_isInterface;
    }

    @Override
    public boolean isComponent() {
        return m_isComponent;
    }

    @Override
    public String getPath() {
        return m_path;
    }

    @Nullable
    @Override
    public VirtualFile getVirtualFile() {
        return m_virtualFile;
    }

    public void setVirtualFile(@NotNull VirtualFile virtualFile) {
        m_virtualFile = virtualFile;
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
