package com.reason.bs;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class BucklescriptErrorsManager {
    /**
     * Returns the document manager instance for the specified project.
     *
     * @param project the project for which the document manager is requested.
     * @return the document manager instance.
     */
    public static BucklescriptErrorsManager getInstance(@NotNull Project project) {
        return project.getComponent(BucklescriptErrorsManager.class);
    }

    abstract public void setError(BsbError error);

    abstract public Collection<BsbError> getError(String filePath);
}
