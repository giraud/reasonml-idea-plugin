package com.reason.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.RmlFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RmlPsiUtil {

    @NotNull
    static List<PsiFile> findFileModules(@NotNull Project project, @NotNull String name) {
        ArrayList<PsiFile> result = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            if (name.equalsIgnoreCase(virtualFile.getNameWithoutExtension())) {
                result.add(PsiManager.getInstance(project).findFile(virtualFile));
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
