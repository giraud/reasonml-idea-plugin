package com.reason;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.build.bs.BucklescriptManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileManager {

    private FileManager() {
    }

    @NotNull
    public static String toRelativeSourceName(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull Path relativePath) {
        String sourcePath = relativePath.toString();
        String namespace = BucklescriptManager.getInstance(project).getNamespace(sourceFile);
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
    public static Path pathFromSource(@NotNull Project project, @NotNull VirtualFile baseRoot, @NotNull Path relativeBuildPath, @NotNull VirtualFile sourceFile, boolean useCmt) {
        Path baseRootPath = FileSystems.getDefault().getPath(baseRoot.getPath());
        Path relativePath;
        try {
            relativePath = baseRootPath.relativize(new File(sourceFile.getPath()).toPath());
        } catch (IllegalArgumentException e) {
            // Path can't be relativized
            return null;
        }

        Path relativeParent = relativePath.getParent();
        if (relativeParent != null) {
            relativeBuildPath = relativeBuildPath.resolve(relativeParent);
        }

        String namespace = BucklescriptManager.getInstance(project).getNamespace(sourceFile);
        return relativeBuildPath.resolve(sourceFile.getNameWithoutExtension() + (namespace.isEmpty() ? "" : "-" + namespace) + (useCmt ? ".cmt" : ".cmi"));
    }

    @Nullable
    public static VirtualFile fromSource(@NotNull Project project, @NotNull VirtualFile baseRoot, @NotNull Path relativeBuildPath, @NotNull VirtualFile sourceFile, boolean useCmt) {
        Path path = pathFromSource(project, baseRoot, relativeBuildPath, sourceFile, useCmt);
        if (path == null) {
            return null;
        }
        String relativeCmiPath = separatorsToUnix(path.toString());
        return baseRoot.findFileByRelativePath(relativeCmiPath);
    }

    private static String separatorsToUnix(String path) {
        return path.replace('\\', '/');
    }
}
