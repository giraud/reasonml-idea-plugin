package com.reason.ide.hints;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.bs.BucklescriptProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

class CmiFileManager { // Transform to a project aware component

    @NotNull
    static String toRelativeSourceName(@NotNull Project project, @NotNull Path relativeCmi) {
        String cmiName = relativeCmi.toString();
        String namespace = BucklescriptProjectComponent.getInstance(project).getNamespace();
        if (!namespace.isEmpty()) {
            cmiName = cmiName.replace("-" + namespace, "");
        }
        return cmiName.replace(".cmi", ".re");
    }

    @Nullable
    static VirtualFile toSource(@NotNull Project project, @NotNull Path relativeCmi) {
        /* ml if re not found ?? */
        String relativeSource = separatorsToUnix(toRelativeSourceName(project, relativeCmi));
        return Platform.findBaseRoot(project).findFileByRelativePath(relativeSource);
    }

    @Nullable
    static VirtualFile fromSource(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String relativeCmiPath = separatorsToUnix(pathFromSource(project, sourceFile).toString());
        return Platform.findBaseRoot(project).findFileByRelativePath(relativeCmiPath);
    }

    static Path pathFromSource(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        Path relativePath = basePath.relativize(new File(sourceFile.getPath()).toPath());

        Path relativeRoot = FileSystems.getDefault().getPath("lib", "bs").resolve(relativePath.getParent());

        String namespace = BucklescriptProjectComponent.getInstance(project).getNamespace();
        return relativeRoot.resolve(sourceFile.getNameWithoutExtension() + (namespace.isEmpty() ? "" : "-" + namespace) + ".cmi");
    }

    private static String separatorsToUnix(String path) {
        return path.replace('\\', '/');
    }
}
