package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reason.lang.RmlTypes.MODULE_PATH;

public class PsiOpen extends ASTWrapperPsiElement implements NavigatablePsiElement {

    public PsiOpen(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        PsiElement name = findChildByType(MODULE_PATH);
        return name == null ? "" : name.getText();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.EXTERNAL;
            }
        };
    }

    @Override
    public String toString() {
        return "Open(" + getName() + ")";
    }
}
