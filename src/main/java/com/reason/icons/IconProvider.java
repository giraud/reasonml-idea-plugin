package com.reason.icons;

import com.intellij.psi.PsiElement;
import com.reason.OclFile;
import com.reason.RmlFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class IconProvider extends com.intellij.ide.IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof RmlFile) {
            return Icons.RML_FILE;
        }
        if (element instanceof OclFile) {
            return Icons.OCL_FILE;
        }
        return null;
    }

}
