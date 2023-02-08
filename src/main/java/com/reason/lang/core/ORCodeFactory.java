package com.reason.lang.core;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class ORCodeFactory {
    private ORCodeFactory() {
    }

    @Nullable
    public static RPsiUpperSymbol createModuleName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "module " + name + " = {};");
        RPsiInnerModule module = ORUtil.findImmediateFirstChildOfClass(file, RPsiInnerModule.class);
        return ORUtil.findImmediateFirstChildOfClass(module, RPsiUpperSymbol.class);
    }

    @Nullable
    public static RPsiLowerSymbol createLetName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "let " + name + " = 1;");
        RPsiLet let = ORUtil.findImmediateFirstChildOfClass(file, RPsiLet.class);
        return ORUtil.findImmediateFirstChildOfClass(let, RPsiLowerSymbol.class);
    }

    @Nullable
    public static RPsiLowerSymbol createTypeName(@NotNull Project project, @NotNull String name) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, "type " + name + ";");
        RPsiType type = ORUtil.findImmediateFirstChildOfClass(file, RPsiType.class);
        return ORUtil.findImmediateFirstChildOfClass(type, RPsiLowerSymbol.class);
    }

    @Nullable
    public static PsiElement createExpression(@NotNull Project project, @NotNull String expression) {
        FileBase file = createFileFromText(project, RmlLanguage.INSTANCE, expression);
        return ORUtil.findImmediateFirstChildWithoutClass(file, RPsiFakeModule.class);
    }

    @NotNull
    public static FileBase createFileFromText(
            @NotNull Project project, @NotNull Language language, @NotNull String text) {
        return (FileBase)
                PsiFileFactory.getInstance(project).createFileFromText("Dummy", language, text);
    }
}
