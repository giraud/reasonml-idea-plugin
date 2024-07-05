package com.reason.ide.structure;

import com.intellij.icons.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ShowVariableFilter implements Filter {
    @Override
    public boolean isVisible(TreeElement treeNode) {
        if (treeNode instanceof StructureViewElement viewElement && viewElement.getElement() instanceof RPsiVar varElement) {
            return varElement.isFunction();
        }
        return true;
    }

    @Override
    public boolean isReverted() {
        return true;
    }

    @Override
    public @NotNull ActionPresentation getPresentation() {
        return new ActionPresentation() {
            @Override
            public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getText() {
                return "Show variables";
            }

            @Override
            public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDescription() {
                return "Show variables";
            }

            @Override
            public @NotNull Icon getIcon() {
                return AllIcons.General.InspectionsEye;
            }
        };
    }

    @Override public @NotNull String getName() {
        return "ShowVariables";
    }
}
