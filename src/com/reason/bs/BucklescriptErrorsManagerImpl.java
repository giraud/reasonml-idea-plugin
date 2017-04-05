package com.reason.bs;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.util.containers.ConcurrentMultiMap;
import com.reason.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class BucklescriptErrorsManagerImpl extends BucklescriptErrorsManager implements ProjectComponent {

    ConcurrentMultiMap<String, BsbError> errorsByFile;

    @NotNull
    @Override
    public String getComponentName() {
        return BucklescriptErrorsManager.class.getSimpleName();
    }

    @Override
    public void projectOpened() {
        this.errorsByFile = new ConcurrentMultiMap<>();
    }

    @Override
    public void projectClosed() {
        this.errorsByFile = null;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public void setError(BsbError error) {
        String canonicalPath = error.getCanonicalPath();
        if (Platform.isWindows()) {
            canonicalPath = canonicalPath.replaceAll("\\\\", "/");
        }
        this.errorsByFile.putValue(canonicalPath, error);
    }

    @Override
    public Collection<BsbError> getError(String filePath) {
        return this.errorsByFile.get(filePath);
    }
}
