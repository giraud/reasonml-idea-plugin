package com.reason.ide;

import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.BitUtil;
import com.reason.Icons;
import com.reason.bs.BsConstants;
import com.reason.ide.files.*;
import com.reason.ide.search.IndexedFileModule;
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
            if (isBsJsFile((PsiFile) element)) {
                return Icons.BS_FILE;
            }
        } else if (element instanceof PsiException) {
            return Icons.EXCEPTION;
        } else if (element instanceof PsiInnerModule) {
            return Icons.MODULE;
        } else if (element instanceof PsiFunctor) {
            return Icons.FUNCTOR;
        } else if (element instanceof PsiType) {
            return Icons.TYPE;
        } else if (element instanceof PsiVariantDeclaration) {
            return Icons.VARIANT;
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            return let.isRecord() ? Icons.OBJECT : (let.isFunction() ? Icons.FUNCTION : Icons.LET);
        } else if (element instanceof PsiExternal) {
            return Icons.EXTERNAL;
        } else if (element instanceof PsiVal) {
            return Icons.VAL;
        }
        return null;
    }

    @Nullable
    public static Icon getFileModuleIcon(@NotNull FileBase element) {
        return getFileModuleIcon(FileHelper.isOCaml(element.getFileType()), FileHelper.isInterface(element.getFileType()));
    }

    public static Icon getFileModuleIcon(boolean isOCaml, boolean isInterface) {
        if (isOCaml) {
            return isInterface ? Icons.OCL_FILE_MODULE_INTERFACE : Icons.OCL_FILE_MODULE;
        } else {
            return isInterface ? Icons.RML_FILE_MODULE_INTERFACE : Icons.RML_FILE_MODULE;
        }
    }

    public static Icon getFileModuleIcon(@NotNull IndexedFileModule indexedFile) {
        return getFileModuleIcon(indexedFile.isOCaml(), indexedFile.isInterface());
    }

    /* needed as plugin.xml's filetype extension does NOT support extensions with multiple "." */
    private static boolean isBsJsFile(PsiFile psiFile) {
        if (psiFile instanceof JSFile) {
            JSFile jsFile = (JSFile) psiFile;
            return jsFile.getName().endsWith("." + BsConstants.BS_JS_FILE_EXTENSION);
        }
        return false;
    }
}
