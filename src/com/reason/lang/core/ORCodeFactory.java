package com.reason.lang.core;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class ORCodeFactory {
    private ORCodeFactory() {
    }

    @Nullable
    public static PsiUpperIdentifier createModuleName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "module " + name + " = {};");
        PsiInnerModule module = ORUtil.findImmediateFirstChildOfClass(file, PsiInnerModule.class);
        return ORUtil.findImmediateFirstChildOfClass(module, PsiUpperIdentifier.class);
    }

    @Nullable
    public static PsiLowerIdentifier createLetName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "let " + name + " = 1;");
        PsiLet let = ORUtil.findImmediateFirstChildOfClass(file, PsiLet.class);
        return ORUtil.findImmediateFirstChildOfClass(let, PsiLowerIdentifier.class);
    }

    @Nullable
    public static PsiElement createTypeName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "type " + name + ";");
        PsiType type = ORUtil.findImmediateFirstChildOfClass(file, PsiType.class);
        return ORUtil.findImmediateFirstChildOfClass(type, PsiLowerIdentifier.class);
    }

    @Nullable
    public static PsiElement createExpression(@NotNull Project project, @NotNull String expression) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, expression);
        return ORUtil.findImmediateFirstChildWithoutClass(file, PsiFakeModule.class);
    }

    @NotNull
    public static FileBase createFileFromText(
            @NotNull Project project, @NotNull Language language, @NotNull String text) {
        return (FileBase)
                PsiFileFactory.getInstance(project).createFileFromText("Dummy", language, text);
    }
}
