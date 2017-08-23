package com.reason.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.RmlFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class RmlPsiUtil {

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
    static List<ReasonMLModule> findModules(@NotNull Project project, @NotNull String name) {
        ArrayList<ReasonMLModule> result = new ArrayList<>();

        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            ReasonMLModule[] modules = PsiTreeUtil.getChildrenOfType(file, ReasonMLModule.class);
            if (modules != null) {
                for (ReasonMLModule module : modules) {
                    if (name.equals(module.getModuleName().getText())) {
                        result.add(module);
                    }
                }
            }
        }

        return result;
    }

    @NotNull
    static List<ReasonMLModule> findModules(@NotNull Project project) {
        ArrayList<ReasonMLModule> result = new ArrayList<>();


        Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
            ReasonMLModule[] modules = PsiTreeUtil.getChildrenOfType(file, ReasonMLModule.class);
            if (modules != null) {
                result.addAll(Arrays.asList(modules));
            }
        }

        return result;
    }
}
