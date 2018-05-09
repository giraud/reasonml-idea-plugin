package com.reason.ide.hints;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.bs.BucklescriptManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CmiFileManager {

    @NotNull
    public static String toRelativeSourceName(@NotNull Project project, @NotNull Path relativeCmi) {
        String cmiName = relativeCmi.toString();
        String namespace = BucklescriptManager.getInstance(project).getNamespace();
        if (!namespace.isEmpty()) {
            cmiName = cmiName.replace("-" + namespace, "");
        }
        return cmiName.replace(".cmi", ".re");
    }

    @Nullable
    public static VirtualFile toSource(@NotNull Project project, @NotNull Path relativeCmi) {
        String relativeSource = separatorsToUnix(toRelativeSourceName(project, relativeCmi));
        VirtualFile sourceFile = Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);

        if (sourceFile == null) {
            /* ml if re not found ?? */
            relativeSource = relativeSource.replace(".re", ".ml");
            sourceFile = Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);
        }

        return sourceFile;
    }

    @Nullable
    static VirtualFile fromSource(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String relativeCmiPath = separatorsToUnix(pathFromSource(project, sourceFile).toString());
        return Platform.findBaseRoot(project).findFileByRelativePath(relativeCmiPath);
    }

    static Path pathFromSource(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path relativeRoot = FileSystems.getDefault().getPath("lib", "bs");

        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        Path relativePath = basePath.relativize(new File(sourceFile.getPath()).toPath());
        Path relativeParent = relativePath.getParent();
        if (relativeParent != null) {
            relativeRoot = relativeRoot.resolve(relativeParent);
        }

        String namespace = BucklescriptManager.getInstance(project).getNamespace();
        return relativeRoot.resolve(sourceFile.getNameWithoutExtension() + (namespace.isEmpty() ? "" : "-" + namespace) + ".cmi");
    }

    private static String separatorsToUnix(String path) {
        return path.replace('\\', '/');
    }
}
