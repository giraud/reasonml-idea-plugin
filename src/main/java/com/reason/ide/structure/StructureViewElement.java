package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.OclFile;
import com.reason.RmlFile;
import com.reason.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private PsiElement element;

    StructureViewElement(PsiElement element) {
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
            if (presentation != null) {
                return presentation;
            }
        }
        throw new RuntimeException("Unknown presentation");
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (element instanceof RmlFile || element instanceof OclFile) {
            PsiModule[] modules = PsiTreeUtil.getChildrenOfType(element, PsiModule.class);
            PsiLet[] lets = PsiTreeUtil.getChildrenOfType(element, PsiLet.class);
            PsiType[] types = PsiTreeUtil.getChildrenOfType(element, PsiType.class);
            PsiExternal[] externals = PsiTreeUtil.getChildrenOfType(element, PsiExternal.class);

            List<TreeElement> treeElements;
            if (modules != null || lets != null || types != null || externals != null) {
                treeElements = new ArrayList<>((modules == null ? 0 : modules.length) + (lets == null ? 0 : lets.length) + (types == null ? 0 : types.length) + (externals == null ? 0 : externals.length));

                if (externals != null) {
                    for (PsiExternal external : externals) {
                        treeElements.add(new StructureViewElement(external));
                    }
                }
                if (types != null) {
                    for (PsiType type : types) {
                        treeElements.add(new StructureViewElement(type));
                    }
                }
                if (modules != null) {
                    for (PsiModule module : modules) {
                        treeElements.add(new StructureViewElement(module));
                    }
                }
                if (lets != null) {
                    for (PsiLet let : lets) {
                        treeElements.add(new StructureViewElement(let));
                    }
                }

                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        } else if (element instanceof PsiModule) {
            List<TreeElement> treeElements = buildModuleStructure((PsiModule) element);
            if (treeElements != null) {
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        }

        return EMPTY_ARRAY;
    }

    @Nullable
    private List<TreeElement> buildModuleStructure(PsiModule moduleElement) {
        PsiScopedExpr moduleBody = moduleElement.getModuleBody();
        PsiExternal[] externals = PsiTreeUtil.getChildrenOfType(moduleBody, PsiExternal.class);
        PsiType[] types = PsiTreeUtil.getChildrenOfType(moduleBody, PsiType.class);
        PsiLet[] lets = PsiTreeUtil.getChildrenOfType(moduleBody, PsiLet.class);
        PsiModule[] modules = PsiTreeUtil.getChildrenOfType(moduleBody, PsiModule.class);

        List<TreeElement> treeElements;
        if (lets != null || types != null || externals != null || modules != null) {
            treeElements = new ArrayList<>((lets == null ? 0 : lets.length) + (types != null ? types.length : 0) + (externals != null ? externals.length : 0));

            if (types != null) {
                for (PsiType type : types) {
                    treeElements.add(new StructureViewElement(type));
                }
            }
            if (externals != null) {
                for (PsiExternal external : externals) {
                    treeElements.add(new StructureViewElement(external));
                }
            }
            if (modules != null) {
                for (PsiModule module : modules) {
                    treeElements.add(new StructureViewElement(module));
                }
            }
            if (lets != null) {
                for (PsiLet let : lets) {
                    treeElements.add(new StructureViewElement(let));
                }
            }

            return treeElements;
        }
        return null;
    }
}
