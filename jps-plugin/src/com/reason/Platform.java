package com.reason;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;

public class Platform {

    public static final String LOCAL_BS_PLATFORM = "/node_modules/bs-platform";
    public static final String LOCAL_NODE_MODULES_BIN = "/node_modules/.bin";
    public static final String PACKAGE_JSON_NAME = "package.json";
    public static final String BSCONFIG_JSON_NAME = "bsconfig.json";
    public static final Charset UTF8 = StandardCharsets.UTF_8;

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

    @Nullable
    public static File getPluginLocation() {
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
        return plugin == null ? null : plugin.getPath();
    }

    @NotNull
    public static Map<Module, VirtualFile> findPackageContentRoots(@NotNull Project project) {
        Map<Module, VirtualFile> rootContents = new HashMap<>();

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (Module module : moduleManager.getModules()) {
            for (VirtualFile contentRoot : ModuleRootManager.getInstance(module).getContentRoots()) {
                VirtualFile packageJson = contentRoot.findChild(PACKAGE_JSON_NAME);
                if (packageJson != null) {
                    rootContents.put(module, packageJson);
                }
            }
        }

        return rootContents;
    }

    @Nullable
    public static VirtualFile findORPackageJsonContentRoot(@NotNull Project project) {
        Map<Module, VirtualFile> rootContents = findPackageContentRoots(project);

        if (rootContents.isEmpty()) {
            LOG.warn("No content roots with package.json file found");
            return null;
        } else if (rootContents.size() == 1) {
            Module module = rootContents.keySet().iterator().next();
            VirtualFile packageJson = rootContents.get(module);
            return packageJson.getParent();
        } else {
            Module module = rootContents.keySet().iterator().next();
            VirtualFile packageJson = rootContents.get(module);
            LOG.info("Many modules with package.json file in it found (" + rootContents.size() + "), using first", rootContents);
            return packageJson.getParent();
        }
    }

    @Nullable
    public static VirtualFile findORPackageJsonContentRoot(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile contentRoot = null;

        Module module = ProjectFileIndex.getInstance(project).getModuleForFile(sourceFile, false);
        if (module != null) {
            for (VirtualFile root : ModuleRootManager.getInstance(module).getContentRoots()) {
                VirtualFile packageJson = root.findChild(PACKAGE_JSON_NAME);
                if (packageJson != null) {
                    contentRoot = packageJson.getParent();
                    break;
                }
            }
        }

        if (contentRoot == null) {
            LOG.trace("Can't find content root from file, using project content root", sourceFile);
            Map<Module, VirtualFile> contentRoots = findPackageContentRoots(project);
            if (contentRoots.isEmpty()) {
                return null;
            } else {
                if (1 < contentRoots.size()) {
                    LOG.info("Multiple package content roots found (" + contentRoots.size() + "), using first one", contentRoots);
                }
                module = contentRoots.keySet().iterator().next();
                contentRoot = contentRoots.get(module).getParent();
            }
        }

        if (contentRoot == null) {
            LOG.info("Can't find content root with a package.json in it, aborting");
            return null;
        }

        return contentRoot;
    }

    @Nullable
    public static VirtualFile findBsconfig(@NotNull Project project) {
        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(project);
        return contentRoot == null ? null : contentRoot.findChild(BSCONFIG_JSON_NAME);
    }

    @Nullable
    public static VirtualFile findBsconfig(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(project, file);
        return contentRoot == null ? null : contentRoot.findChild(BSCONFIG_JSON_NAME);
    }

    @NotNull
    public static String removeProjectDir(@NotNull Project project, @NotNull String path) {
        try {
            VirtualFile baseRoot = Platform.findORPackageJsonContentRoot(project);
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

    // Special finder that iterate through parents until a bsConfig.json is found.
    // This is needed when indexing files in node_modules
    @Nullable
    public static VirtualFile findAncestorBsconfig(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile contentRoot = findBsconfig(project);
        if (sourceFile.equals(contentRoot)) {
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

}
