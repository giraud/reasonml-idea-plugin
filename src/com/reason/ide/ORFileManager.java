package com.reason.ide;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.Platform;
import com.reason.StringUtil;
import com.reason.bs.Bucklescript;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ORFileManager {

    private static final Log LOG = Log.create("manager.file");

    private ORFileManager() {
    }

    @Nullable
    public static PsiFile findCmtFileFromSource(@NotNull Project project, @NotNull String filenameWithoutExtension) {
        if (!DumbService.isDumb(project)) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            String filename = filenameWithoutExtension + ".cmt";

            PsiFile[] cmtFiles = FilenameIndex.getFilesByName(project, filename, scope);
            if (cmtFiles.length == 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("File module for " + filename + " is NOT FOUND, files found: [" + Joiner.join(", ", cmtFiles) + "]");
                }
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

    @NotNull
    public static String toRelativeSourceName(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull Path relativePath) {
        String sourcePath = relativePath.toString();
        String namespace = ServiceManager.getService(project, Bucklescript.class).getNamespace(sourceFile);
        if (!namespace.isEmpty()) {
            sourcePath = sourcePath.replace("-" + StringUtil.toFirstUpper(namespace), "");
        }
        int dotPos = sourcePath.lastIndexOf(".");
        return 0 <= dotPos ? sourcePath.substring(0, dotPos) + ".re" : sourcePath;
    }

    @Nullable
    public static VirtualFile toSource(@NotNull Project project, @NotNull VirtualFile cmxFile, @NotNull Path relativeCmi) {
        String relativeSource = separatorsToUnix(toRelativeSourceName(project, cmxFile, relativeCmi));
        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(project);
        VirtualFile sourceFile = contentRoot == null ? null : contentRoot.findFileByRelativePath(relativeSource);

        if (sourceFile == null && contentRoot != null) {
            relativeSource = relativeSource.replace(".re", ".ml");
            sourceFile = contentRoot.findFileByRelativePath(relativeSource);
        }

        return sourceFile;
    }

    @Nullable
    private static Path pathFromSource(@NotNull Project project, @NotNull VirtualFile baseRoot, @NotNull Path relativeBuildPath,
                                       @NotNull VirtualFile sourceFile, boolean useCmt) {
        Path baseRootPath = FileSystems.getDefault().getPath(baseRoot.getPath());
        Path relativePath;
        try {
            relativePath = baseRootPath.relativize(new File(sourceFile.getPath()).toPath());
        } catch (IllegalArgumentException e) {
            // Path can't be relative
            return null;
        }

        Path relativeParent = relativePath.getParent();
        if (relativeParent != null) {
            relativeBuildPath = relativeBuildPath.resolve(relativeParent);
        }

        String namespace = ServiceManager.getService(project, Bucklescript.class).getNamespace(sourceFile);
        return relativeBuildPath.resolve(sourceFile.getNameWithoutExtension() + (namespace.isEmpty() ? "" : "-" + namespace) + (useCmt ? ".cmt" : ".cmi"));
    }

    @Nullable
    public static VirtualFile fromSource(@NotNull Project project, @NotNull VirtualFile baseRoot, @NotNull Path relativeBuildPath,
                                         @NotNull VirtualFile sourceFile, boolean useCmt) {
        Path path = pathFromSource(project, baseRoot, relativeBuildPath, sourceFile, useCmt);
        if (path == null) {
            return null;
        }
        String relativeCmiPath = separatorsToUnix(path.toString());
        return baseRoot.findFileByRelativePath(relativeCmiPath);
    }

    @NotNull
    private static String separatorsToUnix(@NotNull String path) {
        return path.replace('\\', '/');
    }
}
