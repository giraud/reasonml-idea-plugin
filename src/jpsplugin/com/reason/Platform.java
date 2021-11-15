package jpsplugin.com.reason;

import com.intellij.execution.configurations.*;
import com.intellij.ide.plugins.*;
import com.intellij.openapi.extensions.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Platform {

    public static final Charset UTF8 = StandardCharsets.UTF_8;
    public static final String WINDOWS_EXECUTABLE_SUFFIX = ".exe";

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
    public static Path getPluginLocation() {
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("reasonml"));
        return plugin == null ? null : plugin.getPluginPath();
    }

    public static @NotNull Map<Module, VirtualFile> findContentRootsFor(@NotNull Project project, @NotNull String filename) {
        Map<Module, VirtualFile> rootContents = new HashMap<>();

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (Module module : moduleManager.getModules()) {
            for (VirtualFile contentRoot : ModuleRootManager.getInstance(module).getContentRoots()) {
                VirtualFile child = contentRoot.findChild(filename);
                if (child != null && !rootContents.containsKey(module)) {
                    rootContents.put(module, child);
                }
            }
        }

        return rootContents;
    }

    /**
     * @deprecated replace usages with ORProjectManager::findContentRoots which returns ALL potential
     * roots.
     */
    @Deprecated
    public static @Nullable VirtualFile findContentRootFor(@NotNull Project project, @NotNull String filename) {
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

    public static @NotNull Optional<Path> findExecutableInPath(String filename, String shellPath) {
        if (SystemInfo.isWindows) {
            filename += WINDOWS_EXECUTABLE_SUFFIX;
        }
        File exeFile = PathEnvironmentVariableUtil.findInPath(filename, shellPath, null);
        return exeFile == null ? Optional.empty() : Optional.of(exeFile.toPath());
    }

    public static @NotNull String getRelativePathToModule(@NotNull PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        String relativePath = virtualFile == null ? file.getName() : virtualFile.getPath();

        Module module = ModuleUtil.findModuleForFile(file);
        if (module != null) {
            String fileName = file.getName();
            ModuleRootManagerEx moduleRootManager = ModuleRootManagerEx.getInstanceEx(module);
            for (VirtualFile sourceRoot : moduleRootManager.getSourceRoots()) {
                VirtualFile child = sourceRoot.findChild(fileName);
                if (child != null) {
                    relativePath = child.getPath().replace(sourceRoot.getPath(), sourceRoot.getName());
                    break;
                }
            }
        }

        return relativePath;
    }

    public static @Nullable Module getModule(@NotNull Project project, @Nullable VirtualFile file) {
        PsiFile psiFile = file == null ? null : PsiManager.getInstance(project).findFile(file);
        return psiFile == null ? null : ModuleUtil.findModuleForFile(psiFile);
    }

    public static boolean isSourceFile(@NotNull PsiFile psiFile) {
        Module module = ModuleUtil.findModuleForFile(psiFile);
        ModuleRootManager rootManager = module == null ? null : ModuleRootManager.getInstance(module);
        ModuleFileIndex fileIndex = rootManager == null ? null : rootManager.getFileIndex();

        return fileIndex != null && fileIndex.isInSourceContent(psiFile.getVirtualFile());
    }
}
