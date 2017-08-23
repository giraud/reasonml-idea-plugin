package com.reason.bs;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.util.containers.ConcurrentMultiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BucklescriptErrorsManagerImpl extends BucklescriptErrorsManager implements ProjectComponent {

    private ConcurrentMultiMap<String, BsbError> m_errorsByFile;

    @NotNull
    @Override
    public String getComponentName() {
        return BucklescriptErrorsManager.class.getSimpleName();
    }

    @Override
    public void projectOpened() {
        m_errorsByFile = new ConcurrentMultiMap<>();
    }

    @Override
    public void projectClosed() {
        m_errorsByFile = null;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public void setError(String file, BsbError error) {
        m_errorsByFile.putValue(file, error);
    }

    @Override
    public Collection<BsbError> getError(String filePath) {
        return m_errorsByFile.get(filePath);
    }

    @Override
    public void clearErrors(String fileProcessed) {
        m_errorsByFile.remove(fileProcessed);
    }
}
