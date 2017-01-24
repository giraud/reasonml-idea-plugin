package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.ReasonMLFile;
import com.reason.psi.ReasonMLModuleStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReasonMLStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private PsiElement element;

    ReasonMLStructureViewElement(PsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof NavigationItem) {
            ((NavigationItem) element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element instanceof NavigationItem &&
                ((NavigationItem) element).canNavigateToSource();
    }

    @Override
    public String getAlphaSortKey() {
        return element instanceof PsiNamedElement ? ((PsiNamedElement) element).getName() : null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return element instanceof NavigationItem ? ((NavigationItem) element).getPresentation() : null;
    }

    @Override
    public TreeElement[] getChildren() {
        if (element instanceof ReasonMLFile) {
            ReasonMLModuleStatement[] modules = PsiTreeUtil.getChildrenOfType(element, ReasonMLModuleStatement.class);
            List<TreeElement> treeElements = new ArrayList<>(modules.length);
            for (ReasonMLModuleStatement module : modules) {
                treeElements.add(new ReasonMLStructureViewElement(module));
            }
            return treeElements.toArray(new TreeElement[treeElements.size()]);
        }

        return EMPTY_ARRAY;
    }
}
