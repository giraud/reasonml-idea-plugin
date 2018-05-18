package com.reason.lang.core.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlElementFactory {
    private RmlElementFactory() {
    }

    @Nullable
    static PsiElement createModuleName(Project project, String name) {
        PsiModule dummyModule = createFileFromText(project, "module " + name + " = {};").asModule();
        if (dummyModule != null) {
            return dummyModule.getModules().iterator().next().getNameIdentifier();
        }
        return null;
    }

    static PsiElement createTypeName(Project project, String name) {
        FileBase dummyFile = createFileFromText(project, "type " + name + ";");
        return ((PsiType) dummyFile.getFirstChild()).getNameIdentifier();
    }

    @Nullable
    public static PsiElement createExpression(Project project, String expression) {
        PsiModule dummyModule = createFileFromText(project, expression).asModule();
        if (dummyModule != null) {
            return dummyModule.getFirstChild().getNextSibling();
        }
        return null;
    }

    @NotNull
    private static FileBase createFileFromText(@NotNull Project project, @NotNull String text) {
        return (FileBase) PsiFileFactory.getInstance(project).createFileFromText("Dummy.re", RmlLanguage.INSTANCE, text);
    }
}
