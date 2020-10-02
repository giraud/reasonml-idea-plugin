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
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Platform {

  public static final String PACKAGE_JSON_NAME = "package.json";
  public static final Charset UTF8 = StandardCharsets.UTF_8;
  public static final String WINDOWS_EXECUTABLE_SUFFIX = ".exe";

  private static final Log LOG = Log.create("platform");

  private Platform() {}

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
  public static Path getPluginLocation() {
    IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
    return plugin == null ? null : plugin.getPath().toPath();
  }

  @NotNull
  private static Map<Module, VirtualFile> findContentRootsFor(
      @NotNull Project project, @NotNull String filename) {
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
   * @deprecated replace usages with ORProjectManager::findContentRoots which returns ALL potential
   *     roots.
   */
  public static VirtualFile findContentRootFor(@NotNull Project project, @NotNull String filename) {
    Map<Module, VirtualFile> rootContents = findContentRootsFor(project, filename);

    if (rootContents.isEmpty()) {
      // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/249
      // LOG.warn("No content roots with " + filename + " file found");
      return null;
    } else if (rootContents.size() == 1) {
      Module module = rootContents.keySet().iterator().next();
      VirtualFile file = rootContents.get(module);
      return file.getParent();
    } else {
      Module module = rootContents.keySet().iterator().next();
      VirtualFile file = rootContents.get(module);
      LOG.info(
          "Many modules with "
              + filename
              + " file in it found ("
              + rootContents.size()
              + "), using first",
          rootContents);
      return file.getParent();
    }
  }

  /**
   * Move this out of jps-plugin and replace implementation with ORProjectManager. This incorrectly
   * assumes that the project is a BuckleScript project which might not be the case. Could be Dune,
   * Esy, mono-repo, etc.
   */
  @NotNull
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
