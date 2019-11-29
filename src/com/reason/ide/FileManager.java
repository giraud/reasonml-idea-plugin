package com.reason.ide;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import com.reason.bs.Bucklescript;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.FileModuleIndexService;

public class FileManager {

    private static final Log LOG = Log.create("file");

    private FileManager() {
    }

    @Nullable
    public static PsiFile findCmtFileFromSource(@NotNull Project project, @NotNull FileBase file) {
        // All file names are unique, use that to get the corresponding cmt
        String moduleName = file.asModuleName();

        if (!DumbService.isDumb(project)) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            String filename = FileModuleIndexService.getService().getFilename(moduleName, scope);
            PsiFile[] cmtFiles = FilenameIndex.getFilesByName(project, filename + ".cmt", scope);

            if (cmtFiles.length == 1) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found cmt " + filename + " (" + cmtFiles[0].getVirtualFile().getPath() + ")");
                }
                return cmtFiles[0];
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("File module for " + filename + ".cmt is NOT FOUND, files found: [" + Joiner.join(", ", cmtFiles) + "]");
            }
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
            sourcePath = sourcePath.replace("-" + namespace, "");
        }
        int dotPos = sourcePath.lastIndexOf(".");
        return 0 <= dotPos ? sourcePath.substring(0, dotPos) + ".re" : sourcePath;
    }

    @Nullable
    public static VirtualFile toSource(@NotNull Project project, @NotNull VirtualFile cmxFile, @NotNull Path relativeCmi) {
        String relativeSource = separatorsToUnix(toRelativeSourceName(project, cmxFile, relativeCmi));
        VirtualFile sourceFile = Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);

        if (sourceFile == null) {
            relativeSource = relativeSource.replace(".re", ".ml");
            sourceFile = Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);
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
