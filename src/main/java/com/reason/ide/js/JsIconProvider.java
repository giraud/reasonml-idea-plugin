package com.reason.ide.js;

import com.intellij.lang.javascript.psi.*;
import com.intellij.psi.*;
import com.reason.comp.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class JsIconProvider extends com.intellij.ide.IconProvider {
    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        if (psiElement instanceof PsiFile && isBsJsFile((PsiFile) psiElement)) {
            return ORIcons.BS_FILE;
        }

        return null;
    }

    /* needed as plugin.xml's filetype extension does NOT support extensions with multiple "." */
    private static boolean isBsJsFile(PsiFile psiFile) {
        if (psiFile instanceof JSFile) {
            JSFile jsFile = (JSFile) psiFile;
            return jsFile.getName().endsWith("." + ORConstants.BS_JS_FILE_EXTENSION);
        }
        return false;
    }
}
