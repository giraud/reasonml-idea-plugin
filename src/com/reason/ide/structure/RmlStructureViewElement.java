package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.psi.ReasonMLExternal;
import com.reason.psi.ReasonMLLet;
import com.reason.psi.ReasonMLModule;
import com.reason.psi.ReasonMLScopedExpr;
import com.reason.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RmlStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private PsiElement element;

    RmlStructureViewElement(PsiElement element) {
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

    @NotNull
    @Override
    public String getAlphaSortKey() {
        if (element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) element).getName();
            return name == null ? "" : name;
        }
        return "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (element instanceof NavigationItem) {
            ItemPresentation presentation = ((NavigationItem) element).getPresentation();
            if (presentation != null)
            return presentation;
        }
        throw new RuntimeException("Unknown presentation");
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof RmlFile) {
            ReasonMLModule[] modules = PsiTreeUtil.getChildrenOfType(element, ReasonMLModule.class);
            ReasonMLLet[] lets = PsiTreeUtil.getChildrenOfType(element, ReasonMLLet.class);
            ReasonMLType[] types = PsiTreeUtil.getChildrenOfType(element, ReasonMLType.class);
            ReasonMLExternal[] externals = PsiTreeUtil.getChildrenOfType(element, ReasonMLExternal.class);

            List<TreeElement> treeElements;
            if (modules != null || lets != null || types != null || externals != null) {
                treeElements = new ArrayList<>((modules == null ? 0 : modules.length) + (lets == null ? 0 : lets.length) + (types == null ? 0 : types.length) + (externals == null ? 0 : externals.length));

                if (externals != null) {
                    for (ReasonMLExternal external : externals) {
                        treeElements.add(new RmlStructureViewElement(external));
                    }
                }
                if (types != null) {
                    for (ReasonMLType type : types) {
                        treeElements.add(new RmlStructureViewElement(type));
                    }
                }
                if (modules != null) {
                    for (ReasonMLModule module : modules) {
                        treeElements.add(new RmlStructureViewElement(module));
                    }
                }
                if (lets != null) {
                    for (ReasonMLLet let : lets) {
                        treeElements.add(new RmlStructureViewElement(let));
                    }
                }

                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof ReasonMLModule) {
            List<TreeElement> treeElements = buildModuleStructure((ReasonMLModule) element);
            if (treeElements != null) return treeElements.toArray(new TreeElement[treeElements.size()]);
        }

        return EMPTY_ARRAY;
    }

    @Nullable
    private List<TreeElement> buildModuleStructure(ReasonMLModule moduleElement) {
        ReasonMLScopedExpr moduleBody = moduleElement.getModuleBody();
        ReasonMLExternal[] externals = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLExternal.class);
        ReasonMLType[] types = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLType.class);
        ReasonMLLet[] lets = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLLet.class);
        ReasonMLModule[] modules = PsiTreeUtil.getChildrenOfType(moduleBody, ReasonMLModule.class);

        List<TreeElement> treeElements;
        if (lets != null || types != null || externals != null || modules != null) {
            treeElements = new ArrayList<>((lets == null ? 0 : lets.length) + (types != null ? types.length : 0) + (externals != null ? externals.length : 0));

            if (types != null) {
                for (ReasonMLType type : types) {
                    treeElements.add(new RmlStructureViewElement(type));
                }
            }
            if (externals != null) {
                for (ReasonMLExternal external : externals) {
                    treeElements.add(new RmlStructureViewElement(external));
                }
            }
            if (modules != null) {
                for (ReasonMLModule module : modules) {
                    treeElements.add(new RmlStructureViewElement(module));
                }
            }
            if (lets != null) {
                for (ReasonMLLet let : lets) {
                    treeElements.add(new RmlStructureViewElement(let));
                }
            }

            return treeElements;
        }
        return null;
    }
}
