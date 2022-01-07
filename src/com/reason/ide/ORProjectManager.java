package com.reason.ide;

import com.google.common.collect.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.esy.*;
import com.reason.ide.files.*;
import jpsplugin.com.reason.Platform;
import org.apache.commons.lang3.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.reason.comp.dune.DunePlatform.*;
import static com.reason.comp.esy.EsyConstants.*;

/**
 * Identifies and retrieves modules by project type (Bs, Esy, Dune).
 */
public class ORProjectManager {
    private static final Map<String, Integer> DUNE_PROJECT_FILE_PRIORITY =
            ImmutableMap.of(DUNE_PROJECT_FILENAME, 2, DUNE_FILENAME, 1, LEGACY_JBUILDER_FILENAME, 0);

    private static final Comparator<VirtualFile> DUNE_PROJECT_FILE_COMPARATOR =
            (left, right) -> DUNE_PROJECT_FILE_PRIORITY.get(right.getName()) - DUNE_PROJECT_FILE_PRIORITY.get(left.getName());

    private static final Comparator<VirtualFile> FILE_DEPTH_COMPARATOR = Comparator.comparingInt(ORProjectManager::fileSeparatorCount);

    private ORProjectManager() {
    }

    public static boolean isDuneProject(@NotNull Project project) {
        return !isEsyProject(project) && !findDuneConfigurationFiles(project).isEmpty();
    }

    public static boolean isEsyProject(@NotNull Project project) {
        return !findEsyConfigurationFiles(project).isEmpty();
    }

    public static @NotNull Optional<VirtualFile> findFirstBsConfigurationFile(@NotNull Project project) {
        return findFirstO(findBsConfigurationFiles(project));
    }

    public static LinkedHashSet<VirtualFile> findBsConfigurationFiles(@NotNull Project project) {
        return findFilesInProject("bsconfig.json", project)
                .stream()
                .sorted(FILE_DEPTH_COMPARATOR)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static HashSet<VirtualFile> findDuneConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(DuneFileType.getDefaultFilenames(), project)
                .stream()
                .sorted(FILE_DEPTH_COMPARATOR)
                .sorted(DUNE_PROJECT_FILE_COMPARATOR)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static LinkedHashSet<VirtualFile> findEsyConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(ESY_CONFIG_FILENAME, project)
                .stream()
                .filter(EsyPackageJson::isEsyPackageJson)
                .sorted(FILE_DEPTH_COMPARATOR)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static LinkedHashSet<VirtualFile> findBsContentRoots(@NotNull Project project) {
        return mapToParents(project, ORProjectManager::findBsConfigurationFiles);
    }

    public static LinkedHashSet<VirtualFile> findDuneContentRoots(@NotNull Project project) {
        return mapToParents(project, ORProjectManager::findDuneConfigurationFiles);
    }

    public static Set<VirtualFile> findEsyContentRoots(@NotNull Project project) {
        return mapToParents(project, ORProjectManager::findEsyConfigurationFiles);
    }

    public static Set<VirtualFile> findFilesInProject(@NotNull Set<String> filenames, @NotNull Project project) {
        return filenames
                .stream()
                .map(filename -> findFilesInProject(filename, project))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public static @NotNull Set<VirtualFile> findFilesInProject(@NotNull String filename, @NotNull Project project) {
        Map<Module, VirtualFile> contentRootsFor = Platform.findContentRootsFor(project, filename);
        return contentRootsFor.isEmpty() ? Collections.emptySet() : new HashSet<>(contentRootsFor.values());
    }

    public static @Nullable VirtualFile findFirstBsContentRoot(@NotNull Project project) {
        return findFirst(findBsContentRoots(project));
    }

    public static @Nullable VirtualFile findFirstDuneContentRoot(@NotNull Project project) {
        return findFirst(findDuneContentRoots(project));
    }

    public static @Nullable VirtualFile findFirstDuneConfigFile(@NotNull Project project) {
        return findFirst(findDuneConfigurationFiles(project));
    }

    public static @NotNull Optional<VirtualFile> findFirstEsyContentRoot(@NotNull Project project) {
        return findFirstO(findEsyContentRoots(project));
    }

    private static <T> @NotNull Optional<T> findFirstO(@NotNull Set<T> virtualFiles) {
        Iterator<T> iterator = virtualFiles.iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    private static <T> @Nullable T findFirst(@NotNull Set<T> virtualFiles) {
        Iterator<T> iterator = virtualFiles.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    private static LinkedHashSet<VirtualFile> mapToParents(@NotNull Project project, @NotNull Function<Project, HashSet<VirtualFile>> findConfigurationFiles) {
        return findConfigurationFiles
                .apply(project)
                .stream()
                .map(VirtualFile::getParent)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static int fileSeparatorCount(@NotNull VirtualFile file) {
        return StringUtils.countMatches(file.getPath(), '/');
    }
}
