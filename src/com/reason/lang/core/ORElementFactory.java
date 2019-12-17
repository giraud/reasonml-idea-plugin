package com.reason.lang.core;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORElementFactory {
    private ORElementFactory() {
    }

    @Nullable
    public static PsiElement createModuleName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "module " + name + " = {};");
        return ((PsiInnerModule) file.getFirstChild()).getNameIdentifier();
    }

    @Nullable
    public static PsiElement createLetName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "let " + name + " = 1;");
        return ((PsiLet) file.getFirstChild()).getNameIdentifier();
    }

    @Nullable
    public static PsiElement createTypeName(@NotNull Project project, @NotNull String name) {
        FileBase dummyFile = createFileFromText(project, RmlLanguage.INSTANCE, "type " + name + ";");
        return ((PsiType) dummyFile.getFirstChild()).getNameIdentifier();
    }

    @Nullable
    public static PsiElement createExpression(@NotNull Project project, @NotNull String expression) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, expression);
        return file.getFirstChild();
    }

    @NotNull
    public static FileBase createFileFromText(@NotNull Project project, @NotNull Language language, @NotNull String text) {
        return (FileBase) PsiFileFactory.getInstance(project).createFileFromText("Dummy", language, text);
    }
}
