package com.reason.lang.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.OclFileType;
import com.reason.ide.files.RmlFileType;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RmlPsiUtil {

    public static String fileNameToModuleName(VirtualFile file) {
        return fileNameToModuleName(file.getName());
    }

    public static String fileNameToModuleName(PsiFile file) {
        return fileNameToModuleName(file.getName());
        //String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(file.getName());
        //return nameWithoutExtension.substring(0, 1).toUpperCase(Locale.getDefault()) + nameWithoutExtension.substring(1);
    }

    public static String fileNameToModuleName(String filename) {
        String nameWithoutExtension = FileUtilRt.getNameWithoutExtension(filename);
        if (nameWithoutExtension.isEmpty()) {
            return "";
        }
        return nameWithoutExtension.substring(0, 1).toUpperCase(Locale.getDefault()) + nameWithoutExtension.substring(1);
    }

    @NotNull
    public static List<PsiFile> findFileModules(@NotNull Project project, String extension, @NotNull String name, boolean exact) {
        ArrayList<PsiFile> result = new ArrayList<>();

        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);
        PsiManager psiManager = PsiManager.getInstance(project);

        Collection<VirtualFile> files = FilenameIndex.getAllFilesByExt(project, extension);
        for (VirtualFile vFile : files) {
            String canonicalPath = vFile.getCanonicalPath();
            if (bucklescript.isDependency(canonicalPath)) {
                FileBase file = (FileBase) psiManager.findFile(vFile);
                if (file != null) {
                    String fileModuleName = file.asModuleName();
                    boolean found = exact ? fileModuleName.equals(name) : fileModuleName.startsWith(name);
                    if (found) {
                        result.add(file);
                    }
                }
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
                    if (name.equals(module.getName())) {
                        result.add(module);
                    }
                }
            }
        }

        return result;
    }

    @Nullable
    public static PsiFile findFileModule(Project project, String name) {
        List<PsiFile> rmlModules = findFileModules(project, RmlFileType.INSTANCE.getDefaultExtension(), name, true);
        if (rmlModules.size() == 1) {
            return rmlModules.get(0);
        }

        List<PsiFile> oclModules = findFileModules(project, OclFileType.INSTANCE.getDefaultExtension(), name, true);
        if (oclModules.size() == 1) {
            return oclModules.get(0);
        }

        return null;
    }

    @NotNull
    public static List<PsiModule> findModules(@NotNull Project project) {
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

    @NotNull
    public static TextRange getTextRangeForReference(@NotNull PsiNamedElement name) {
        PsiElement nameIdentifier = name.getNameIdentifier();
        return rangeInParent(name.getTextRange(), nameIdentifier == null ? TextRange.EMPTY_RANGE : name.getTextRange());
    }

    @NotNull
    private static TextRange rangeInParent(@NotNull TextRange parent, @NotNull TextRange child) {
        int start = child.getStartOffset() - parent.getStartOffset();
        return TextRange.create(start, start + child.getLength());
    }
}
