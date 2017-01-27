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
import com.reason.psi.ReasonMLLetBinding;
import com.reason.psi.ReasonMLModuleStatement;
import com.reason.psi.ReasonMLTypeStatement;

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
            ReasonMLLetBinding[] lets = PsiTreeUtil.getChildrenOfType(element, ReasonMLLetBinding.class);
            ReasonMLTypeStatement[] types = PsiTreeUtil.getChildrenOfType(element, ReasonMLTypeStatement.class);

            List<TreeElement> treeElements;
            if (modules != null || lets != null || types != null) {
                treeElements = new ArrayList<>((modules == null ? 0 : modules.length) + (lets == null ? 0 : lets.length) + (types != null ? types.length : 0));

                if (modules != null) {
                    for (ReasonMLModuleStatement module : modules) {
                        treeElements.add(new ReasonMLStructureViewElement(module));
                    }
                }
                if (types != null) {
                    for (ReasonMLTypeStatement type : types) {
                        treeElements.add(new ReasonMLStructureViewElement(type));
                    }
                }
                if (lets != null) {
                    for (ReasonMLLetBinding let : lets) {
                        treeElements.add(new ReasonMLStructureViewElement(let));
                    }
                }

                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        }

        return EMPTY_ARRAY;
    }
}
