package com.reason.ide;

import com.intellij.json.psi.JsonFile;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.BitUtil;
import com.reason.bs.BsConstants;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.files.*;
import com.reason.ide.search.IndexedFileModule;
import com.reason.lang.core.psi.*;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IconProvider extends com.intellij.ide.IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement psiElement, int flags) {
        PsiElement element = psiElement instanceof PsiFakeModule ? psiElement.getContainingFile() : psiElement;
        if (element instanceof PsiFile) {
            if (element instanceof OclFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? ORIcons.OCL_FILE_MODULE : ORIcons.OCL_FILE;
            }
            if (element instanceof OclInterfaceFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? ORIcons.OCL_FILE_MODULE_INTERFACE : ORIcons.OCL_INTERFACE_FILE;
            }
            if (element instanceof RmlFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? ORIcons.RML_FILE_MODULE : ORIcons.RML_FILE;
            }
            if (element instanceof RmlInterfaceFile) {
                return BitUtil.isSet(flags, Iconable.ICON_FLAG_VISIBILITY) ? ORIcons.RML_FILE_MODULE_INTERFACE : ORIcons.RML_INTERFACE_FILE;
            }
            if (isBsJsFile((PsiFile) element)) {
                return ORIcons.BS_FILE;
            }
            if (isEsyPackageJson((PsiFile) element)) {
                return ORIcons.ESY_FILE;
            }
        } else if (element instanceof PsiException) {
            return ORIcons.EXCEPTION;
        } else if (element instanceof PsiInnerModule) {
            return ORIcons.MODULE;
        } else if (element instanceof PsiFunctor) {
            return ORIcons.FUNCTOR;
        } else if (element instanceof PsiType) {
            return ORIcons.TYPE;
        } else if (element instanceof PsiVariantDeclaration) {
            return ORIcons.VARIANT;
        } else if (element instanceof PsiLet) {
            PsiLet let = (PsiLet) element;
            return let.isRecord() ? ORIcons.OBJECT : (let.isFunction() ? ORIcons.FUNCTION : ORIcons.LET);
        } else if (element instanceof PsiExternal) {
            return ORIcons.EXTERNAL;
        } else if (element instanceof PsiVal) {
            return ORIcons.VAL;
        }
        return null;
    }

    @Nullable
    public static Icon getFileModuleIcon(@NotNull FileBase element) {
        return getFileModuleIcon(FileHelper.isOCaml(element.getFileType()), FileHelper.isInterface(element.getFileType()));
    }

    public static Icon getFileModuleIcon(boolean isOCaml, boolean isInterface) {
        if (isOCaml) {
            return isInterface ? ORIcons.OCL_FILE_MODULE_INTERFACE : ORIcons.OCL_FILE_MODULE;
        } else {
            return isInterface ? ORIcons.RML_FILE_MODULE_INTERFACE : ORIcons.RML_FILE_MODULE;
        }
    }

    public static Icon getFileModuleIcon(@NotNull IndexedFileModule indexedFile) {
        return getFileModuleIcon(indexedFile.isOCaml(), indexedFile.isInterface());
    }

    private boolean isEsyPackageJson(PsiFile element) {
        return element instanceof JsonFile && EsyPackageJson.isEsyPackageJson(element.getVirtualFile());
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
