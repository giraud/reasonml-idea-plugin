package com.reason.ide;

import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.BitUtil;
import com.reason.Icons;
import com.reason.ide.files.*;
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
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? Icons.OCL_FILE_MODULE : Icons.OCL_FILE;
            }
            if (element instanceof OclInterfaceFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? Icons.OCL_FILE_MODULE_INTERFACE : Icons.OCL_INTERFACE_FILE;
            }
            if (element instanceof RmlFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? Icons.RML_FILE_MODULE : Icons.RML_FILE;
            }
            if (element instanceof RmlInterfaceFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? Icons.RML_FILE_MODULE_INTERFACE : Icons.RML_INTERFACE_FILE;
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

    @Nullable
    public static Icon getFileModuleIcon(@NotNull FileBase element) {
        if (element instanceof OclFile) {
            return Icons.OCL_FILE_MODULE;
        }
        if (element instanceof OclInterfaceFile) {
            return Icons.OCL_FILE_MODULE_INTERFACE;
        }
        if (element instanceof RmlFile) {
            return Icons.RML_FILE_MODULE;
        }
        if (element instanceof RmlInterfaceFile) {
            return Icons.RML_FILE_MODULE_INTERFACE;
        }
        return null;
    }
}
