package com.reason.ide;

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.bs.BsConstants;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.files.BsConfigJsonFileType;
import com.reason.ide.files.DuneFileType;
import com.reason.ide.files.EsyPackageJsonFileType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.reason.bs.BsConstants.BS_PLATFORM_DIRECTORY_NAME;
import static com.reason.dune.DuneConstants.*;

/**
 * Identifies and retrieves modules by project type (Bs, Esy, Dune).
 */
public class ORProjectManager {

    private static final Map<String, Integer> DUNE_PROJECT_FILE_PRIORITY = ImmutableMap
            .of(DUNE_PROJECT_FILENAME, 2, DUNE_FILENAME, 1, LEGACY_JBUILDER_FILENAME, 0);

    private static final Comparator<VirtualFile> DUNE_PROJECT_FILE_COMPARATOR = (left, right) ->
            DUNE_PROJECT_FILE_PRIORITY.get(right.getName()) - DUNE_PROJECT_FILE_PRIORITY.get(left.getName());

    private static final Comparator<VirtualFile> FILE_DEPTH_COMPARATOR =
            Comparator.comparingInt(ORProjectManager::fileSeparatorCount);

    private ORProjectManager() {}

    public static boolean isBsProject(@NotNull Project project) {
        return !findBsConfigurationFiles(project).isEmpty();
    }

    public static boolean isDuneProject(@NotNull Project project) {
        return !isEsyProject(project) && !findDuneConfigurationFiles(project).isEmpty();
    }

    public static boolean isEsyProject(@NotNull Project project) {
        return !findEsyConfigurationFiles(project).isEmpty();
    }

    public static Optional<VirtualFile> findFirstBsConfigurationFile(@NotNull Project project) {
        return findFirst(findBsConfigurationFiles(project));
    }

    public static LinkedHashSet<VirtualFile> findBsConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(BsConfigJsonFileType.getDefaultFilename(), project).stream()
                .sorted(FILE_DEPTH_COMPARATOR)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static LinkedHashSet<VirtualFile> findDuneConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(DuneFileType.getDefaultFilenames(), project).stream()
                .sorted(FILE_DEPTH_COMPARATOR)
                .sorted(DUNE_PROJECT_FILE_COMPARATOR)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static LinkedHashSet<VirtualFile> findEsyConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(EsyPackageJsonFileType.getDefaultFilename(), project).stream()
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
        return filenames.stream()
                .map(filename -> findFilesInProject(filename, project))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public static Set<VirtualFile> findFilesInProject(@NotNull String filename, @NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        Collection<VirtualFile> virtualFilesByName = FilenameIndex.getVirtualFilesByName(project, filename, scope);
        return new HashSet<>(virtualFilesByName);
    }

    public static Optional<VirtualFile> findFirstBsContentRoot(@NotNull Project project) {
        return findFirst(findBsContentRoots(project));
    }

    public static Optional<VirtualFile> findFirstDuneContentRoot(@NotNull Project project) {
        return findFirst(findDuneContentRoots(project));
    }

    public static Optional<VirtualFile> findFirstEsyContentRoot(@NotNull Project project) {
        return findFirst(findEsyContentRoots(project));
    }

    private static <T> Optional<T> findFirst(@NotNull Set<T> virtualFiles) {
        Iterator<T> iterator = virtualFiles.iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    private static LinkedHashSet<VirtualFile> mapToParents(@NotNull Project project,
            @NotNull Function<Project, LinkedHashSet<VirtualFile>> findConfigurationFiles) {
        return findConfigurationFiles.apply(project).stream()
                .map(VirtualFile::getParent)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static int fileSeparatorCount(@NotNull VirtualFile file) {
        return StringUtils.countMatches(file.getPath(), '/');
    }
}
