package com.reason;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Platform {

    public static final String LOCAL_BS_PLATFORM = "/node_modules/bs-platform";
    public static final String LOCAL_NODE_MODULES_BIN = "/node_modules/.bin";
    public static final String PACKAGE_JSON_NAME = "package.json";
    public static final String BSCONFIG_JSON_NAME = "bsconfig.json";
    public static final Charset UTF8 = StandardCharsets.UTF_8;
    public static final String WINDOWS_EXECUTABLE_SUFFIX =  ".exe";

    private static final Log LOG = Log.create("platform");

    private Platform() {
    }

    @NotNull
    public static String getOsPrefix() {
        if (SystemInfo.isWindows) {
            return "w";
        }

        if (SystemInfo.isLinux) {
            return "l";
        }

        if (SystemInfo.isMac) {
            return "o";
        }

        return "";
    }

    public static boolean isWindows() {
        return SystemInfo.isWindows;
    }

    @Nullable
    public static File getPluginLocation() {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
        return plugin == null ? null : plugin.getPath();
    }

    @NotNull
    private static Map<Module, VirtualFile> findContentRootsFor(@NotNull Project project, @NotNull String filename) {
        Map<Module, VirtualFile> rootContents = new HashMap<>();

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (Module module : moduleManager.getModules()) {
            for (VirtualFile contentRoot : ModuleRootManager.getInstance(module).getContentRoots()) {
                VirtualFile packageJson = contentRoot.findChild(filename);
                if (packageJson != null && !rootContents.containsKey(module)) {
                    rootContents.put(module, packageJson);
                }
            }
        }

        return rootContents;
    }

    /**
     * @deprecated replace usages with ORProjectManager::findContentRoots which returns ALL potential roots.
     */
    public static VirtualFile findContentRootFor(@NotNull Project project, @NotNull String filename) {
        Map<Module, VirtualFile> rootContents = findContentRootsFor(project, filename);

        if (rootContents.isEmpty()) {
            LOG.warn("No content roots with " + filename + " file found");
            return null;
        } else if (rootContents.size() == 1) {
            Module module = rootContents.keySet().iterator().next();
            VirtualFile file = rootContents.get(module);
            return file.getParent();
        } else {
            Module module = rootContents.keySet().iterator().next();
            VirtualFile file = rootContents.get(module);
            LOG.info("Many modules with " + filename + " file in it found (" + rootContents.size() + "), using first", rootContents);
            return file.getParent();
        }
    }


    /**
     * Project Special finder that iterate through parents until a bsConfig.json is found.
     * This is always needed, we can't use module itself.
     * @deprecated move this logic out of jps-plugin. Break it up into 2 separate methods:
     *  1. ORFileManager::findFirstAncestor
     *  2. ORProjectManager::findAncestorBsConfig
     *  This method currently used findContentRootsFor which is deprecated as a project might have multiple
     *  content roots.
     */
    @Nullable
    @Deprecated
    public static VirtualFile findAncestorBsconfig(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile contentRoot = findContentRootFor(project, BSCONFIG_JSON_NAME);
        if (sourceFile.equals(contentRoot)) {
            if (contentRoot.isDirectory()) {
                return contentRoot.findChild(BSCONFIG_JSON_NAME);
            }
            return sourceFile;
        }

        VirtualFile parent = sourceFile.getParent();
        if (parent == null) {
            return sourceFile;
        }

        VirtualFile child = parent.findChild(BSCONFIG_JSON_NAME);
        while (child == null) {
            VirtualFile grandParent = parent.getParent();
            if (grandParent == null) {
                break;
            }

            parent = grandParent;
            child = parent.findChild(BSCONFIG_JSON_NAME);
            if (parent.equals(contentRoot)) {
                break;
            }
        }

        return child;
    }

    /**
     * @deprecated see {@link Platform::findAncestorBsconfig}
     */
    @Deprecated
    public static VirtualFile findAncestorContentRoot(Project project, VirtualFile file) {
        VirtualFile bsConfig = findAncestorBsconfig(project, file);
        return bsConfig == null ? null : bsConfig.getParent();
    }

    /**
     * @deprecated Move this out of jps-plugin and replace implementation with ORProjectManager.
     *             This incorrectly assumes that the project is a BuckleScript project which might
     *             not be the case. Could be Dune, Esy, mono-repo, etc.
     */
    @NotNull
    @Deprecated
    public static String removeProjectDir(@NotNull Project project, @NotNull String path) {
        try {
            VirtualFile baseRoot = findContentRootFor(project, PACKAGE_JSON_NAME);
            if (baseRoot == null) {
                return path;
            }
            Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
            Path relativePath = basePath.relativize(new File(path).toPath());
            return relativePath.toString();
        } catch (IllegalArgumentException e) {
            return path;
        }
    }

    @Nullable
    public static VirtualFile findFileByRelativePath(@NotNull Project project, @NotNull String path) {
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            VirtualFile moduleFile = module.getModuleFile();
            VirtualFile baseDir = moduleFile == null ? null : moduleFile.getParent();
            VirtualFile file = baseDir == null ? null : baseDir.findFileByRelativePath(path);
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    public static Optional<Path> findExecutableInPath(String filename, String shellPath) {
      if (SystemInfo.isWindows) {
        filename += WINDOWS_EXECUTABLE_SUFFIX;
      }
      File exeFile = PathEnvironmentVariableUtil.findInPath(filename, shellPath, null);
      return exeFile == null ? Optional.empty() : Optional.of(exeFile.toPath());
    }
}
