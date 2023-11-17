package com.reason.comp.esy;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.ide.settings.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.esy.EsyConstants.*;

public class EsyPlatform {
    private EsyPlatform() {
    }

    public static @NotNull List<VirtualFile> findConfigFiles(@NotNull Project project) {
        return BsPlatform.findConfigFiles(project);
    }

    public static boolean isEsyProject(@NotNull Project project) {
        return !findEsyConfigurationFiles(project).isEmpty();
    }

    public static List<VirtualFile> findEsyConfigurationFiles(@NotNull Project project) {
        return FilenameIndex.getVirtualFilesByName(ESY_CONFIG_FILENAME, GlobalSearchScope.projectScope(project))
                .stream()
                .filter(EsyPackageJson::isEsyPackageJson)
                .sorted(ORFileUtils.FILE_DEPTH_COMPARATOR)
                .toList();
    }

    public static List<VirtualFile> findEsyContentRoots(@NotNull Project project) {
        return findEsyConfigurationFiles(project)
                .stream()
                .map(VirtualFile::getParent)
                .toList();
    }

    public static Optional<VirtualFile> findEsyExecutable(@NotNull Project project) {
        String esyExecutable = project.getService(ORSettings.class).getEsyExecutable();
        if (esyExecutable.isEmpty()) {
            return Esy.findEsyExecutable();
        }
        return Optional.ofNullable(LocalFileSystem.getInstance().findFileByPath(esyExecutable));
    }
}
