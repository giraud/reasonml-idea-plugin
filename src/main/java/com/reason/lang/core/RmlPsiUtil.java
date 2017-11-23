package com.reason.lang.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.RmlFileType;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RmlPsiUtil {

    @NotNull
    public static List<PsiFile> findFileModules(@NotNull Project project, String extension, @NotNull String name) {
        ArrayList<PsiFile> result = new ArrayList<>();

        Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, extension);
        for (VirtualFile file : files) {
            if (file.getNameWithoutExtension().toLowerCase(Locale.getDefault()).startsWith(name)) {
                result.add(PsiManager.getInstance(project).findFile(file));
            }
        }

        return result;
    }

    @NotNull
    public static List<PsiModule> findModules(@NotNull Project project, @NotNull String name) {
        ArrayList<PsiModule> result = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            PsiModule[] modules = PsiTreeUtil.getChildrenOfType(file, PsiModule.class);
            if (modules != null) {
                for (PsiModule module : modules) {
                    if (name.equals(module.getModuleName().getText())) {
                        result.add(module);
                    }
                }
            }
        }

        return result;
    }

    @NotNull
    static List<PsiModule> findModules(@NotNull Project project) {
        ArrayList<PsiModule> result = new ArrayList<>();


        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            PsiModule[] modules = PsiTreeUtil.getChildrenOfType(file, PsiModule.class);
            if (modules != null) {
                result.addAll(Arrays.asList(modules));
            }
        }

        return result;
    }
}
