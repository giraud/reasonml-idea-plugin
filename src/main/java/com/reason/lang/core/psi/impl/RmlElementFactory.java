package com.reason.lang.core.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.reason.ide.files.RmlFile;
import com.reason.ide.files.RmlFileType;
import com.reason.lang.RmlLanguage;
import com.reason.lang.core.psi.PsiType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class RmlElementFactory {
    private RmlElementFactory() {
    }

    static PsiElement createModuleName(Project project, String name) {
        RmlFile dummyFile = createFileFromText(project, "module " + name + " = {};");
        return dummyFile.asModule().getModules().iterator().next().getNameIdentifier();
    }

    public static PsiElement createTypeName(Project project, String name) {
        RmlFile dummyFile = createFileFromText(project, "type " + name + ";");
        return ((PsiType) dummyFile.getFirstChild()).getNameIdentifier();
    }

    @NotNull
    private static RmlFile createFileFromText(@NotNull Project project, @NotNull String text) {
        @NonNls String filename = "dummy." + RmlFileType.INSTANCE.getDefaultExtension();
        return (RmlFile) PsiFileFactory.getInstance(project).createFileFromText(filename, RmlLanguage.INSTANCE, text);
    }
}
