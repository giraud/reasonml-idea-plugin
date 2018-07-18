package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiStructuredElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final PsiElement m_element;

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
        if (m_element instanceof FileBase) {
            List<TreeElement> treeElements = new ArrayList<>();
            m_element.acceptChildren(new ElementVisitor(treeElements));
            return treeElements.toArray(new TreeElement[0]);
        } else if (m_element instanceof PsiModule) {
            List<TreeElement> treeElements = buildModuleStructure((PsiModule) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        }

        return EMPTY_ARRAY;
    }

    @NotNull
    private List<TreeElement> buildModuleStructure(PsiModule moduleElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = moduleElement.getSignature();
        if (rootElement == null) {
            rootElement = moduleElement.getBody();
        }

        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements));
        }

        return treeElements;
    }

    static class ElementVisitor extends PsiElementVisitor {
        private final List<TreeElement> m_treeElements;

        ElementVisitor(List<TreeElement> elements) {
            m_treeElements = elements;
        }

        @Override
        public void visitElement(PsiElement element) {
            if (element instanceof PsiLet) {
                PsiLet let = (PsiLet) element;
                if (let.getName() != null && !let.getName().isEmpty()) {
                    m_treeElements.add(new StructureViewElement(element));
                }
            } else if (element instanceof PsiStructuredElement) {
                m_treeElements.add(new StructureViewElement(element));
            }
        }
    }

}
