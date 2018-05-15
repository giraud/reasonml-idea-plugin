package com.reason;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.BucklescriptManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileManager {

    private FileManager() {
    }

    @NotNull
    public static String toRelativeSourceName(@NotNull Project project, @NotNull Path relativePath) {
        String sourcePath = relativePath.toString();
        String namespace = BucklescriptManager.getInstance(project).getNamespace();
        if (!namespace.isEmpty()) {
            sourcePath = sourcePath.replace("-" + namespace, "");
        }
        int dotPos = sourcePath.lastIndexOf(".");
        return 0 <= dotPos ? sourcePath.substring(0, dotPos) + ".re" : sourcePath;
    }

    @Nullable
    public static VirtualFile toSource(@NotNull Project project, @NotNull Path relativeCmi) {
        String relativeSource = separatorsToUnix(toRelativeSourceName(project, relativeCmi));
        VirtualFile sourceFile = Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);

        if (sourceFile == null) {
            relativeSource = relativeSource.replace(".re", ".ml");
            sourceFile = Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);
        }

        return sourceFile;
    }

    @Nullable
    public static VirtualFile fromSource(@NotNull Project project, @NotNull VirtualFile sourceFile, boolean useCmt) {
        String relativeCmiPath = separatorsToUnix(pathFromSource(project, sourceFile, useCmt).toString());
        return Platform.findBaseRoot(project).findFileByRelativePath(relativeCmiPath);
    }

    public static Path pathFromSource(@NotNull Project project, @NotNull VirtualFile sourceFile, boolean useCmt) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path relativeRoot = FileSystems.getDefault().getPath("lib", "bs");

        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        Path relativePath = basePath.relativize(new File(sourceFile.getPath()).toPath());
        Path relativeParent = relativePath.getParent();
        if (relativeParent != null) {
            relativeRoot = relativeRoot.resolve(relativeParent);
        }

        String namespace = BucklescriptManager.getInstance(project).getNamespace();
        return relativeRoot.resolve(sourceFile.getNameWithoutExtension() + (namespace.isEmpty() ? "" : "-" + namespace) + (useCmt ? ".cmt" : ".cmi"));
    }

    private static String separatorsToUnix(String path) {
        return path.replace('\\', '/');
    }
}
