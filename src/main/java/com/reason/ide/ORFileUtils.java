package com.reason.ide;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.comp.bs.*;
import jpsplugin.com.reason.*;
import org.apache.commons.lang3.*;
import org.jetbrains.annotations.*;

import java.nio.file.*;
import java.util.*;

public class ORFileUtils {
    public static final Comparator<VirtualFile> FILE_DEPTH_COMPARATOR = Comparator.comparingInt(file -> StringUtils.countMatches(file.getPath(), '/'));

    private static final Log LOG = Log.create("file");

    private ORFileUtils() {
    }

    public static VirtualFile findCmtFileFromSource(@NotNull Project project, @NotNull String filenameWithoutExtension, @Nullable String namespace) {
        if (!DumbService.isDumb(project)) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            String filename = (namespace == null ? "" : namespace) + filenameWithoutExtension + ".cmt";

            Collection<VirtualFile> cmtFiles = FilenameIndex.getVirtualFilesByName(filename, scope);
            if (cmtFiles.isEmpty()) {
                LOG.debug("File module NOT FOUND", filename);
                return null;
            }

            VirtualFile firstFile = cmtFiles.iterator().next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found cmt " + filename + " (" + firstFile.getPath() + ")");
            }

            return firstFile;
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
     * @param start   starting directory (or file) to begin searching for `target`
     * @param target  file being searched for
     * @return found target file
     */
    public static @Nullable VirtualFile findAncestor(@NotNull Project project, @Nullable VirtualFile start, @NotNull String target) {
        if (start == null) {
            return null;
        }

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
        return findAncestor(project, parent, target);
    }

    public static @Nullable VirtualFile findOneOfAncestor(@NotNull Project project, @Nullable VirtualFile start, @NotNull String target1, @NotNull String target2) {
        if (start == null) {
            return null;
        }

        // start must be a directory, should only happen on first iteration
        if (!start.isDirectory()) {
            String startName = start.getName();
            if (target1.equals(startName) || target2.equals(startName)) {
                return start;
            }

            return oneOfAncestor(project.getBasePath(), start.getParent(), target1, target2);
        }

        return oneOfAncestor(project.getBasePath(), start, target1, target2);
    }

    public static @Nullable VirtualFile oneOfAncestor(@Nullable String basePath, @Nullable VirtualFile start, @NotNull String target1, @NotNull String target2) {
        if (start == null) {
            return null;
        }

        // target found, done

        VirtualFile foundTarget1 = start.findChild(target1);
        if (foundTarget1 != null) {
            return foundTarget1;
        }
        VirtualFile foundTarget2 = start.findChild(target2);
        if (foundTarget2 != null) {
            return foundTarget2;
        }

        // just checked project root, done

        if (start.getPath().equals(basePath)) {
            return null;
        }

        // Continue

        return oneOfAncestor(basePath, start.getParent(), target1, target2);
    }

    // RELEASE: check no direct getVirtualFile() in code
    // Because psiFile.getVirtualFile() is not annotated with @Nullable !
    public static @Nullable VirtualFile getVirtualFile(@Nullable PsiFile psiFile) {
        VirtualFile virtualFile = psiFile == null ? null : psiFile.getVirtualFile();
        // If file is memory only, use original file
        if (psiFile != null && virtualFile == null) {
            virtualFile = psiFile.getOriginalFile().getVirtualFile();
        }
        return virtualFile;
    }
}
