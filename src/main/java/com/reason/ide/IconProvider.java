package com.reason.ide;

import com.intellij.json.psi.*;
import com.intellij.psi.*;
import com.reason.comp.esy.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class IconProvider extends com.intellij.ide.IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof PsiFile) {
            if (element instanceof OclFile) {
                return ORIcons.OCL_FILE;
            }
            if (element instanceof OclInterfaceFile) {
                return ORIcons.OCL_INTERFACE_FILE;
            }
            if (element instanceof RmlFile) {
                return ORIcons.RML_FILE;
            }
            if (element instanceof RmlInterfaceFile) {
                return ORIcons.RML_INTERFACE_FILE;
            }
            if (element instanceof ResFile) {
                return ORIcons.RES_FILE;
            }
            if (element instanceof ResInterfaceFile) {
                return ORIcons.RES_INTERFACE_FILE;
            }
            if (isEsyPackageJson((PsiFile) element)) {
                return ORIcons.ESY_FILE;
            }
        } else if (element instanceof RPsiException) {
            return ORIcons.EXCEPTION;
        } else if (element instanceof RPsiInnerModule) {
            return ((RPsiInnerModule) element).isInterface() ? ORIcons.INNER_MODULE_INTF : ORIcons.INNER_MODULE;
        } else if (element instanceof RPsiFunctor) {
            return ORIcons.FUNCTOR;
        } else if (element instanceof RPsiType) {
            return ORIcons.TYPE;
        } else if (element instanceof RPsiVariantDeclaration) {
            return ORIcons.VARIANT;
        } else if (element instanceof RPsiLet let) {
            return let.isRecord() ? ORIcons.OBJECT : (let.isFunction() ? ORIcons.FUNCTION : ORIcons.LET);
        } else if (element instanceof RPsiExternal) {
            return ORIcons.EXTERNAL;
        } else if (element instanceof RPsiVal) {
            return ORIcons.VAL;
        }
        return null;
    }

    public static @NotNull Icon getDataModuleIcon(@NotNull FileModuleData element) {
        boolean isInterface = element.isInterface();
        if (element.isOCaml()) {
            return isInterface ? ORIcons.OCL_FILE_MODULE_INTERFACE : ORIcons.OCL_FILE_MODULE;
        } else if (element.isRescript()) {
            return isInterface ? ORIcons.RES_FILE_MODULE_INTERFACE : ORIcons.RES_FILE_MODULE;
        } else {
            return isInterface ? ORIcons.RML_FILE_MODULE_INTERFACE : ORIcons.RML_FILE_MODULE;
        }
    }

    public static @NotNull Icon getDataModuleFileIcon(@NotNull FileModuleData element) {
        boolean isInterface = element.isInterface();
        if (element.isOCaml()) {
            return isInterface ? ORIcons.OCL_INTERFACE_FILE : ORIcons.OCL_FILE;
        } else if (element.isRescript()) {
            return isInterface ? ORIcons.RES_INTERFACE_FILE : ORIcons.RES_FILE;
        } else {
            return isInterface ? ORIcons.RML_INTERFACE_FILE : ORIcons.RML_FILE;
        }
    }

    private boolean isEsyPackageJson(@Nullable PsiFile element) {
        return element instanceof JsonFile && EsyPackageJson.isEsyPackageJson(ORFileUtils.getVirtualFile(element));
    }
}
