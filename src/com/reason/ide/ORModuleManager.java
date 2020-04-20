package com.reason.ide;

import com.google.common.collect.ImmutableSet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Log;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.files.BsConfigJsonFileType;
import com.reason.ide.files.EsyPackageJsonFileType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.reason.dune.DuneConstants.*;

/**
 * Identifies and retrieves modules by framework type (Bs, Esy, Dune).
 * Prefer this class to access {@link ModuleManager} directly.
 */
public class ORModuleManager {

    private static final Set<String> DUNE_PROJECT_FILES = ImmutableSet.of(
            DUNE_FILENAME,
            DUNE_PROJECT_FILENAME,
            LEGACY_JBUILDER_FILENAME);

    private static final Log LOG = Log.create("manager.module");

    private ORModuleManager() {}

    public static boolean isBsModule(@NotNull Module module) {
        return findBsConfigurationFile(module).isPresent();
    }

    public static boolean isDuneModule(@NotNull Module module) {
        return findDuneConfigurationFile(module).isPresent();
    }

    public static boolean isEsyModule(@NotNull Module module) {
        return findEsyConfigurationFile(module).isPresent();
    }

    public static Optional<VirtualFile> findBsConfigurationFile(@NotNull Module module) {
        return findFileInModule(BsConfigJsonFileType.getDefaultFilename(), module);
    }

    /* searches module for files named "dune-project", then "dune", and lastly "jbuild" */
    public static Optional<VirtualFile> findDuneConfigurationFile(@NotNull Module module) {
        Map<String, VirtualFile> foundFiles = findFilesInModule(DUNE_PROJECT_FILES, module).stream()
                .collect(Collectors.toMap(VirtualFile::getName, Function.identity()));
        // first, if no matches were found, return empty...
        if (foundFiles.isEmpty()) {
            return Optional.empty();
        }
        // then let's see if "dune-project" was found...
        VirtualFile foundFile = foundFiles.get(DUNE_PROJECT_FILENAME);
        if (foundFile != null) {
            return Optional.of(foundFile);
        }
        // next, let's check for a "dune" file...
        foundFile = foundFiles.get(DUNE_FILENAME);
        if (foundFile != null) {
            return Optional.of(foundFile);
        }
        // finally, a "jbuild" file...
        foundFile = foundFiles.get(LEGACY_JBUILDER_FILENAME);
        if (foundFile != null) {
            return Optional.of(foundFile);
        }
        // if we get here, something went wrong...
        LOG.warn("Something went wrong. Dune file might have been removed while searching?");
        return Optional.empty();
    }

    public static Optional<VirtualFile> findEsyConfigurationFile(@NotNull Module module) {
        Optional<VirtualFile> file = findFileInModule(EsyPackageJsonFileType.getDefaultFilename(), module);
        if (file.isPresent() && EsyPackageJson.isEsyPackageJson(file.get())) {
            return file;
        }
        return Optional.empty();
    }

    public static Set<Module> findBsModules(@NotNull Project project) {
        return getAllModulesAsStream(project)
                .filter(ORModuleManager::isBsModule)
                .collect(Collectors.toSet());
    }

    @Deprecated
    public static Optional<Module> findFirstBsModule(@NotNull Project project) {
        return findFirst(findBsModules(project));
    }

    public static Set<Module> findDuneModules(@NotNull Project project) {
        return getAllModulesAsStream(project)
                .filter(ORModuleManager::isDuneModule)
                .collect(Collectors.toSet());
    }

    public static Set<Module> findEsyModules(@NotNull Project project) {
        return getAllModulesAsStream(project)
                .filter(ORModuleManager::isEsyModule)
                .collect(Collectors.toSet());
    }

    public static Stream<Module> getAllModulesAsStream(@NotNull Project project) {
        return Stream.of(ModuleManager.getInstance(project).getModules());
    }

    public static Set<VirtualFile> findBsContentRoots(@NotNull Project project) {
        return findContentRoots(project, ORModuleManager::findBsConfigurationFile);
    }

    public static Set<VirtualFile> findDuneContentRoots(@NotNull Project project) {
        return findContentRoots(project, ORModuleManager::findDuneConfigurationFile);
    }

    public static Set<VirtualFile> findEsyContentRoots(@NotNull Project project) {
        return findContentRoots(project, ORModuleManager::findEsyConfigurationFile);
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

    private static Set<VirtualFile> findContentRoots(@NotNull Project project,
            Function<Module, Optional<VirtualFile>> findConfigurationFile) {
        return getAllModulesAsStream(project)
                .map(findConfigurationFile)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(VirtualFile::getParent)
                .collect(Collectors.toSet());
    }

    private static <T> Optional<T> findFirst(Set<T> virtualFiles) {
        Iterator<T> iterator = virtualFiles.iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    private static Optional<VirtualFile> findFileInModule(@NotNull String filename, @NotNull Module module) {
        for (VirtualFile contentRoot : ModuleRootManager.getInstance(module).getContentRoots()) {
            VirtualFile file = contentRoot.findChild(filename);
            if (file != null) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    private static Set<VirtualFile> findFilesInModule(@NotNull Set<String> filenames, @NotNull Module module) {
        Set<VirtualFile> foundFiles = new HashSet<>(filenames.size());
        Set<String> remainingFilenames = new HashSet<>(filenames);
        for (VirtualFile contentRoot : ModuleRootManager.getInstance(module).getContentRoots()) {
            for (Iterator<String> iterator = remainingFilenames.iterator(); iterator.hasNext();) {
                String filename = iterator.next();
                VirtualFile file = contentRoot.findChild(filename);
                if (file != null) {
                    foundFiles.add(file);
                    iterator.remove();
                }
            }
        }
        return foundFiles;
    }
}
