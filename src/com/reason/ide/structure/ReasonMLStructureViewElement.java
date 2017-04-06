package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.*;

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
            ReasonMLLetStatement[] lets = PsiTreeUtil.getChildrenOfType(element, ReasonMLLetStatement.class);
            ReasonMLTypeStatement[] types = PsiTreeUtil.getChildrenOfType(element, ReasonMLTypeStatement.class);
            ReasonMLExternalStatement[] externals = PsiTreeUtil.getChildrenOfType(element, ReasonMLExternalStatement.class);

            List<TreeElement> treeElements;
            if (modules != null || lets != null || types != null || externals != null) {
                treeElements = new ArrayList<>((modules == null ? 0 : modules.length) + (lets == null ? 0 : lets.length) + (types == null ? 0 : types.length) + (externals == null ? 0 : externals.length));

                if (externals != null) {
                    for (ReasonMLExternalStatement external : externals) {
                        treeElements.add(new ReasonMLStructureViewElement(external));
                    }
                }
                if (types != null) {
                    for (ReasonMLTypeStatement type : types) {
                        treeElements.add(new ReasonMLStructureViewElement(type));
                    }
                }
                if (modules != null) {
                    for (ReasonMLModuleStatement module : modules) {
                        treeElements.add(new ReasonMLStructureViewElement(module));
                    }
                }
                if (lets != null) {
                    for (ReasonMLLetStatement let : lets) {
                        treeElements.add(new ReasonMLStructureViewElement(let));
                    }
                }

                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof ReasonMLModuleStatement) {
            ReasonMLModuleBody moduleBody = ((ReasonMLModuleStatement) element).getModuleBody();
            ReasonMLExternalStatement[] externals = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLExternalStatement.class);
            ReasonMLTypeStatement[] types = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLTypeStatement.class);
            ReasonMLLetStatement[] lets = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLLetStatement.class);

            List<TreeElement> treeElements;
            if (lets != null || types != null || externals != null) {
                treeElements = new ArrayList<>((lets == null ? 0 : lets.length) + (types != null ? types.length : 0) + (externals != null ? externals.length : 0));

                if (externals != null) {
                    for (ReasonMLExternalStatement external : externals) {
                        treeElements.add(new ReasonMLStructureViewElement(external));
                    }
                }
                if (types != null) {
                    for (ReasonMLTypeStatement type : types) {
                        treeElements.add(new ReasonMLStructureViewElement(type));
                    }
                }
                if (lets != null) {
                    for (ReasonMLLetStatement let : lets) {
                        treeElements.add(new ReasonMLStructureViewElement(let));
                    }
                }

                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        }

        return EMPTY_ARRAY;
    }
}
