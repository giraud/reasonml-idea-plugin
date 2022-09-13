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
    public static PsiUpperSymbol createModuleName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "module " + name + " = {};");
        PsiInnerModule module = ORUtil.findImmediateFirstChildOfClass(file, PsiInnerModule.class);
        return ORUtil.findImmediateFirstChildOfClass(module, PsiUpperSymbol.class);
    }

    @Nullable
    public static PsiLowerSymbol createLetName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "let " + name + " = 1;");
        PsiLet let = ORUtil.findImmediateFirstChildOfClass(file, PsiLet.class);
        return ORUtil.findImmediateFirstChildOfClass(let, PsiLowerSymbol.class);
    }

    @Nullable
    public static PsiLowerSymbol createTypeName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "type " + name + ";");
        PsiType type = ORUtil.findImmediateFirstChildOfClass(file, PsiType.class);
        return ORUtil.findImmediateFirstChildOfClass(type, PsiLowerSymbol.class);
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
