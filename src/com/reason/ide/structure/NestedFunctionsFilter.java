package com.reason.ide.structure;

import com.intellij.icons.*;
import com.intellij.ide.util.treeView.smartTree.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class NestedFunctionsFilter implements Filter {
    @Override
    public boolean isVisible(TreeElement treeNode) {
        if (treeNode instanceof StructureViewElement) {
            StructureViewElement viewElement = (StructureViewElement) treeNode;
            return viewElement.getLevel() < 2;
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
            public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getText() {
                return "Show nested functions";
            }

            @Override
            public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getDescription() {
                return "Show nested functions";
            }

            @Override
            public @NotNull Icon getIcon() {
                return AllIcons.General.InspectionsEye;
            }
        };
    }

    @Override public @NotNull String getName() {
        return "ShowNestedFunctions";
    }
}
