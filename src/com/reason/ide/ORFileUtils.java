package com.reason.ide;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.comp.bs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.nio.file.*;

public class ORFileUtils {
    private static final Log LOG = Log.create("file");

    private ORFileUtils() {
    }

    public static @Nullable PsiFile findCmtFileFromSource(@NotNull Project project, @NotNull String filenameWithoutExtension, @Nullable String namespace) {
        if (!DumbService.isDumb(project)) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            String filename = (namespace == null ? "" : namespace) + filenameWithoutExtension + ".cmt";

            PsiFile[] cmtFiles = FilenameIndex.getFilesByName(project, filename, scope);
            if (cmtFiles.length == 0) {
                LOG.debug("File module NOT FOUND", filename);
                return null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Found cmt " + filename + " (" + cmtFiles[0].getVirtualFile().getPath() + ")");
            }

            return cmtFiles[0];
        } else {
            LOG.info("Cant find cmt while reindexing");
        }

        return null;
    }

    public static @NotNull String toRelativeSourceName(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull Path relativePath) {
        String sourcePath = relativePath.toString();
        String namespace = project.getService(BsCompiler.class).getNamespace(sourceFile);
        if (!namespace.isEmpty()) {
            sourcePath = sourcePath.replace("-" + StringUtil.toFirstUpper(namespace), "");
        }
        int dotPos = sourcePath.lastIndexOf(".");
        return 0 <= dotPos ? sourcePath.substring(0, dotPos) + ".re" : sourcePath;
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
