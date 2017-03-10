package com.reason.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.tree.IElementType;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reason.psi.ReasonMLTypes.EQUAL;

public class ReasonMLPsiImplUtil {
    public static ItemPresentation getPresentation(final ReasonMLModuleStatement module) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return module.getModuleName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
//                PsiFile containingFile = module.getContainingFile();
//                return containingFile == null ? null : containingFile.getName();
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.MODULE;
            }
        };
    }

    public static ItemPresentation getPresentation(final ReasonMLLetStatement let) {
        ReasonMLLetBindingBody body = let.getLetBinding().getLetBindingBody();
        IElementType elementType = null;
        if (body != null) {
            elementType = body.getFirstChild().getNode().getElementType();
        }
        boolean isField = elementType == null || EQUAL.equals(elementType);

        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                String letName = let.getLetBinding().getValueName().getText();
                if (isField) {
                    return letName;
                }

                return letName + "(..)";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return isField ? ReasonMLIcons.LET : ReasonMLIcons.FUNCTION;
            }
        };
    }

    public static ItemPresentation getPresentation(final ReasonMLTypeStatement type) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return type.getTypeConstrName().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.TYPE;
            }
        };
    }

    public static ItemPresentation getPresentation(final ReasonMLExternalStatement external) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
//                ReasonMLExternalAlias externalAlias = external.getExternalAlias();
                String externalName = external.getValueName().getText();
//                if (externalAlias == null) {
                    return externalName;
//                }

//                String externalAliasText = externalAlias.getText();
//                String externalAliasName = externalAliasText.substring(1, externalAliasText.length() - 1);
//                return externalAliasName.equals(externalName) ? externalName : externalAliasName + " -> " + externalName;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.EXTERNAL;
            }
        };
    }

}
