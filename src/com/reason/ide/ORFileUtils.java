package com.reason.ide;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

public class ORFileUtils {
    private ORFileUtils() {
    }

    /**
     * Given a `start` directory (or file), searches that directory for `target`, continuing up
     * directories until either `target` is found or the project base path is reached.
     *
     * @param project project to use
     * @param target  file being searched for
     * @param start   starting directory (or file) to begin searching for `target`
     * @return found target file
     */
    public static @Nullable VirtualFile findAncestor(@NotNull Project project, @NotNull String target, @NotNull VirtualFile start) {
        // start must be a directory, should only happen on first iteration
        if (!start.isDirectory()) {
            if (target.equals(start.getName())) {
                return start;
            }

            start = start.getParent();
            // parent is null, done
            if (start == null) {
                return null;
            }
        }
        // target found, done
        VirtualFile foundTarget = start.findChild(target);
        if (foundTarget != null) {
            return foundTarget;
        }
        // just checked project root, done
        if (start.getPath().equals(project.getBasePath())) {
            return null;
        }
        // parent is null, we're done
        VirtualFile parent = start.getParent();
        if (parent == null) {
            return null;
        }
        return findAncestor(project, target, parent);
    }
}
