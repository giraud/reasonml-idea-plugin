package com.reason.lang.core;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.ide.files.RmlFileType;
import com.reason.lang.core.psi.Module;

public class RmlPsiUtil {

    public static String fileNameToModuleName(PsiFile file) {
        String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(file.getName());
        return nameWithoutExtension.substring(0, 1).toUpperCase(Locale.getDefault()) + nameWithoutExtension.substring(1);
    }

    @NotNull
    public static List<PsiFile> findFileModules(@NotNull Project project, String extension, @NotNull String name) {
        ArrayList<PsiFile> result = new ArrayList<>();

        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);

        Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, extension);
        for (VirtualFile file : files) {
            String canonicalPath = file.getCanonicalPath();
            if (bucklescript.isDependency(canonicalPath)) {
                if (file.getNameWithoutExtension().toLowerCase(Locale.getDefault()).startsWith(name)) {
                    result.add(PsiManager.getInstance(project).findFile(file));
                }
            }
        }

        return result;
    }

    @NotNull
    public static List<Module> findModules(@NotNull Project project, @NotNull String name) {
        ArrayList<Module> result = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            Module[] modules = PsiTreeUtil.getChildrenOfType(file, Module.class);
            if (modules != null) {
                for (Module module : modules) {
                    if (name.equals(module.getName())) {
                        result.add(module);
                    }
                }
            }
        }

        return result;
    }

    @NotNull
    static List<Module> findModules(@NotNull Project project) {
        ArrayList<Module> result = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            Module[] modules = PsiTreeUtil.getChildrenOfType(file, Module.class);
            if (modules != null) {
                result.addAll(Arrays.asList(modules));
            }
        }

        return result;
    }
}
