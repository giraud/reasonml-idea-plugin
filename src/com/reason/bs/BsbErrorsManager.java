package com.reason.bs;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class BsbErrorsManager {
    /**
     * Returns the document manager instance for the specified project.
     *
     * @param project the project for which the document manager is requested.
     * @return the document manager instance.
     */
    public static BsbErrorsManager getInstance(@NotNull Project project) {
        return project.getComponent(BsbErrorsManager.class);
    }

    abstract public void setError(String file, BsbError error);

    abstract public Collection<BsbError> getErrors(String filePath);

    public void clearErrors(String fileProcessed) {
    }

    static class BsbError {
        public String errorType;
        public int line;
        public int colStart;
        public int colEnd;
        public String message;

        @Override
        public String toString() {
            return "BsbError{" +
                    errorType +
                    ": L" + line + " " + colStart + ":" + colEnd +
                    ", " + message + '}';
        }
    }

}
