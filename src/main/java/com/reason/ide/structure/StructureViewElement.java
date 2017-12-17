package com.reason.ide.structure;

import java.util.*;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.RmlFile;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private PsiElement m_element;

    StructureViewElement(PsiElement element) {
        m_element = element;
    }

    @Override
    public Object getValue() {
        return m_element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (m_element instanceof NavigationItem) {
            ((NavigationItem) m_element).navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return m_element instanceof NavigationItem && ((NavigationItem) m_element).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return m_element instanceof NavigationItem && ((NavigationItem) m_element).canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        if (m_element instanceof PsiNamedElement) {
            String name = ((PsiNamedElement) m_element).getName();
            return name == null ? "" : name;
        }
        return "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (m_element instanceof NavigationItem) {
            ItemPresentation presentation = ((NavigationItem) m_element).getPresentation();
            if (presentation != null) {
                return presentation;
            }
        }
        throw new RuntimeException("Unknown presentation");
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (m_element instanceof RmlFile || m_element instanceof OclFile) {
            List<TreeElement> treeElements = new ArrayList<>();

            m_element.acceptChildren(new PsiElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    if (element instanceof NavigatablePsiElement && !(element instanceof PsiScopedExpr)) {
                        treeElements.add(new StructureViewElement(element));
                    }
                }
            });

            return treeElements.toArray(new TreeElement[treeElements.size()]);
        } else if (m_element instanceof PsiModule) {
            List<TreeElement> treeElements = buildModuleStructure((PsiModule) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[treeElements.size()]);
            }
        }

        return EMPTY_ARRAY;
    }

    @NotNull
    private List<TreeElement> buildModuleStructure(PsiModule moduleElement) {
        List<TreeElement> treeElements = new ArrayList<>();
        PsiScopedExpr moduleBody = moduleElement.getModuleBody();

        moduleBody.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof PsiExternal || element instanceof PsiException || element instanceof PsiLet
                        || element instanceof PsiType || element instanceof PsiModule) {
                    treeElements.add(new StructureViewElement(element));
                }
            }
        });

        return treeElements;
    }
}
