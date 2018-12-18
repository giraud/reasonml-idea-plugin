package com.reason.icons;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.OclInterfaceFile;
import com.reason.ide.files.RmlFile;
import com.reason.ide.files.RmlInterfaceFile;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IconProvider extends com.intellij.ide.IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof PsiFile) {
            if (element instanceof OclFile) {
                return Icons.OCL_FILE;
            }
            if (element instanceof OclInterfaceFile) {
                return Icons.OCL_INTERFACE_FILE;
            }
            if (element instanceof RmlFile) {
                return Icons.RML_FILE;
            }
            if (element instanceof RmlInterfaceFile) {
                return Icons.RML_INTERFACE_FILE;
            }
        } else if (element instanceof PsiModule) {
            return Icons.MODULE;
        } else if (element instanceof PsiFunctor) {
            return Icons.FUNCTOR;
        } else if (element instanceof PsiType) {
            return Icons.TYPE;
        } else if (element instanceof PsiVariantConstructor) {
            return Icons.VARIANT;
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            return let.isObject() ? Icons.OBJECT : (let.isFunction() ? Icons.FUNCTION : Icons.LET);
        } else if (element instanceof PsiExternal) {
            return Icons.EXTERNAL;
        } else if (element instanceof PsiVal) {
            return Icons.VAL;
        }
        return null;
    }
}
