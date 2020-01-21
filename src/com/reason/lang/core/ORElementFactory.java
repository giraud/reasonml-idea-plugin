package com.reason.lang.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.reason.RmlLanguage;

public class ORElementFactory {
    private ORElementFactory() {
    }

    @Nullable
    public static PsiElement createModuleName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "module " + name + " = {};");
        PsiInnerModule firstChild = ORUtil.findImmediateFirstChildOfClass(file, PsiInnerModule.class);
        return firstChild == null ? null : firstChild.getNameIdentifier();
    }

    @Nullable
    public static PsiElement createLetName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "let " + name + " = 1;");
        PsiLet firstChild = ORUtil.findImmediateFirstChildOfClass(file, PsiLet.class);
        return firstChild == null ? null : firstChild.getNameIdentifier();
    }

    @Nullable
    public static PsiElement createTypeName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "type " + name + ";");
        PsiType firstChild = ORUtil.findImmediateFirstChildOfClass(file, PsiType.class);
        return firstChild == null ? null : firstChild.getNameIdentifier();
    }

    @Nullable
    public static PsiElement createExpression(@NotNull Project project, @NotNull String expression) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, expression);
        return ORUtil.findImmediateFirstChildWithoutClass(file, PsiFakeModule.class);
    }

    @NotNull
    public static FileBase createFileFromText(@NotNull Project project, @NotNull Language language, @NotNull String text) {
        return (FileBase) PsiFileFactory.getInstance(project).createFileFromText("Dummy", language, text);
    }
}
