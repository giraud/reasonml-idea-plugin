package com.reason.module;

import com.intellij.openapi.util.Comparing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.ex.JpsElementBase;

public class OCamlBinaryRootProperties extends JpsElementBase<OCamlBinaryRootProperties> {
    private String m_relativeOutputPath = "";
    private boolean m_forGeneratedSources;

    public OCamlBinaryRootProperties(@NotNull String relativeOutputPath, boolean forGeneratedSources) {
        m_relativeOutputPath = relativeOutputPath;
        m_forGeneratedSources = forGeneratedSources;
    }

    @NotNull
    public String getRelativeOutputPath() {
        return m_relativeOutputPath;
    }

    @NotNull
    @Override
    public OCamlBinaryRootProperties createCopy() {
        return new OCamlBinaryRootProperties(m_relativeOutputPath, m_forGeneratedSources);
    }

    public boolean isForGeneratedSources() {
        return m_forGeneratedSources;
    }

    public void setRelativeOutputPath(@NotNull String relativeOutputPath) {
        if (!Comparing.equal(m_relativeOutputPath, relativeOutputPath)) {
            m_relativeOutputPath = relativeOutputPath;
            fireElementChanged();
        }
    }

    public void setForGeneratedSources(boolean forGeneratedSources) {
        if (m_forGeneratedSources != forGeneratedSources) {
            m_forGeneratedSources = forGeneratedSources;
            fireElementChanged();
        }
    }

    @Override
    public void applyChanges(@NotNull OCamlBinaryRootProperties modified) {
        setRelativeOutputPath(modified.m_relativeOutputPath);
        setForGeneratedSources(modified.m_forGeneratedSources);
    }
}
