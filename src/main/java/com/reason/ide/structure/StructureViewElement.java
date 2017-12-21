package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiStructuredElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
                    if (element instanceof PsiStructuredElement) {
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
        PsiElement rootElement = moduleElement.getSignature();
        if (rootElement == null) {
            rootElement = moduleElement.getBody();
        }

        if (rootElement != null) {
            rootElement.acceptChildren(new PsiElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    if (element instanceof PsiStructuredElement) {
                        treeElements.add(new StructureViewElement(element));
                    }
                }
            });
        }

        return treeElements;
    }
}
