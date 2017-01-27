package com.reason.psi.impl;

import com.intellij.navigation.ItemPresentation;
import com.reason.ide.ReasonMLIcons;
import com.reason.psi.ReasonMLLetBinding;
import com.reason.psi.ReasonMLModuleStatement;
import com.reason.psi.ReasonMLTypeStatement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLPsiImplUtil {
    public static ItemPresentation getPresentation(final ReasonMLModuleStatement module) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return module.getModuleIdent().getText();
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

    public static ItemPresentation getPresentation(final ReasonMLLetBinding let) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return let.getShortId().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ReasonMLIcons.LET;
            }
        };
    }

    public static ItemPresentation getPresentation(final ReasonMLTypeStatement type) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return type.getShortId().getText();
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
}
