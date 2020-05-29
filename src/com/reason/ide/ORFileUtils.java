package com.reason.ide;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ORFileUtils {

    private ORFileUtils() {}

    public static Optional<VirtualFile> findAncestorRecursive(@NotNull Project project,
            @NotNull String target, @NotNull VirtualFile start) {
        // start must be a directory, should only happen on first iteration
        if (!start.isDirectory()) {
            start = start.getParent();
            // parent is null, done
            if (start == null) {
                return Optional.empty();
            }
        }
        // target found, done
        VirtualFile foundTarget = start.findChild(target);
        if (foundTarget != null) {
            return Optional.of(foundTarget);
        }
        // just checked project root, done
        if (start.getPath().equals(project.getBasePath())) {
            return Optional.empty();
        }
        // parent is null, we're done
        VirtualFile parent = start.getParent();
        if (parent == null) {
            return Optional.empty();
        }
        return findAncestorRecursive(project, target, parent);
    }
}
