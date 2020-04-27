package com.reason.ide;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.Log;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.files.BsConfigJsonFileType;
import com.reason.ide.files.EsyPackageJsonFileType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.reason.dune.DuneConstants.*;

/**
 * Identifies and retrieves modules by project type (Bs, Esy, Dune).
 */
public class ORProjectManager {

    private static final Set<String> DUNE_PROJECT_FILES = ImmutableSet.of(
            DUNE_FILENAME,
            DUNE_PROJECT_FILENAME,
            LEGACY_JBUILDER_FILENAME
    );

    private static final Map<String, Integer> DUNE_PROJECT_FILE_PRIORITY = ImmutableMap.of(
            DUNE_PROJECT_FILENAME, 2,
            DUNE_FILENAME, 1,
            LEGACY_JBUILDER_FILENAME, 0
    );

    private static final Comparator<VirtualFile> DUNE_PROJECT_FILE_COMPARATOR = (left, right) ->
            DUNE_PROJECT_FILE_PRIORITY.get(right.getName()) - DUNE_PROJECT_FILE_PRIORITY.get(left.getName());

    private static final Log LOG = Log.create("manager.project");

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

    public static Set<VirtualFile> findBsConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(BsConfigJsonFileType.getDefaultFilename(), project);
    }

    public static LinkedHashSet<VirtualFile> findDuneConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(DUNE_PROJECT_FILES, project).stream()
                .sorted(DUNE_PROJECT_FILE_COMPARATOR)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<VirtualFile> findEsyConfigurationFiles(@NotNull Project project) {
        return findFilesInProject(EsyPackageJsonFileType.getDefaultFilename(), project).stream()
                .filter(EsyPackageJson::isEsyPackageJson)
                .collect(Collectors.toSet());
    }

    public static Set<VirtualFile> findBsContentRoots(@NotNull Project project) {
        return findContentRoots(project, ORProjectManager::findBsConfigurationFiles);
    }

    public static LinkedHashSet<VirtualFile> findDuneContentRoots(@NotNull Project project) {
        return findContentRootsPreserveOrder(project, ORProjectManager::findDuneConfigurationFiles);
    }

    public static Set<VirtualFile> findEsyContentRoots(@NotNull Project project) {
        return findContentRoots(project, ORProjectManager::findEsyConfigurationFiles);
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

    /**
     * @deprecated there could be more than 1 BuckleScript configurations in a project.
     */
    @Deprecated
    public static Optional<VirtualFile> findFirstBsContentRoot(@NotNull Project project) {
        LOG.warn("Using deprecated method 'findFirstBsContentRoot'.");
        return findFirst(findBsContentRoots(project));
    }

    /**
     * @deprecated there could be more than 1 Dune configurations in a project.
     */
    @Deprecated
    public static Optional<VirtualFile> findFirstDuneContentRoot(@NotNull Project project) {
        LOG.warn("Using deprecated method 'findFirstDuneContentRoot'.");
        return findFirst(findDuneContentRoots(project));
    }

    /**
     * @deprecated there could be more than 1 Esy configurations in a project.
     */
    @Deprecated
    public static Optional<VirtualFile> findFirstEsyContentRoot(@NotNull Project project) {
        LOG.warn("Using deprecated method 'findFirstEsyContentRoot'.");
        return findFirst(findEsyContentRoots(project));
    }

    private static <T> Optional<T> findFirst(Set<T> virtualFiles) {
        Iterator<T> iterator = virtualFiles.iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    private static Set<VirtualFile> findContentRoots(@NotNull Project project,
           Function<Project, Set<VirtualFile>> findConfigurationFiles) {
        return findConfigurationFiles.apply(project).stream()
                .map(VirtualFile::getParent)
                .collect(Collectors.toSet());
    }

    private static LinkedHashSet<VirtualFile> findContentRootsPreserveOrder(@NotNull Project project,
            Function<Project, LinkedHashSet<VirtualFile>> findConfigurationFiles) {
        return findConfigurationFiles.apply(project).stream()
                .map(VirtualFile::getParent)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
