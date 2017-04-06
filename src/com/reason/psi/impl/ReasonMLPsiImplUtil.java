package com.reason.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.reason.icons.ReasonMLIcons;
import com.reason.psi.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                ReasonMLLetBinding letBinding = let.getLetBinding();
                String letName = letBinding.getLetName().getText();
                if (isFunction(letBinding)) {
                    return letName + (let.hasInferredType() ? ": " + let.getInferredType() : "");
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
                return !isFunction(let.getLetBinding()) ? ReasonMLIcons.FUNCTION : ReasonMLIcons.LET;
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
                ReasonMLExternalAlias externalAlias = external.getExternalAlias();
                String externalName = external.getValueName().getText();
                if (externalAlias.getTextLength() == 2) {
                    return externalName;
                }

                String externalAliasText = externalAlias.getText();
                String externalAliasName = externalAliasText.substring(1, externalAliasText.length() - 1);
                return externalName + (externalAliasName.equals(externalName) ? "" : " ⇐ " + externalAliasName);
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

    public static boolean isFunction(final ReasonMLLetBinding letBinding) {
        boolean[] result = new boolean[]{true};

        letBinding.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                //noinspection unchecked
                if (element.getNode().getElementType() == ReasonMLTypes.EQUAL) {
                    result[0] = false;
                    stopWalking();
                } else if (element.getNode().getElementType() == ReasonMLTypes.ARROW) {
                    result[0] = true;
                    stopWalking();
                } else {
                    super.visitElement(element);
                }
            }
        });

        return result[0];
    }
}
