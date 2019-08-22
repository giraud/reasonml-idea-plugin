package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.PsiIconUtil;
import com.reason.Icons;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.ocamlyacc.OclYaccHeader;
import com.reason.lang.core.psi.ocamlyacc.OclYaccTrailer;
import com.reason.lang.ocamlyacc.OclYaccTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final PsiElement m_element;
    private final PsiElement m_viewElement;

    StructureViewElement(@NotNull PsiElement element) {
        this(null, element);
    }

    private StructureViewElement(@Nullable PsiElement viewElement, @NotNull PsiElement element) {
        m_element = element;
        m_viewElement = viewElement;
    }

    @Override
    public Object getValue() {
        return m_viewElement == null ? m_element : m_viewElement;
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
        String name = null;
        PsiElement element = m_viewElement == null ? m_element : m_viewElement;

        if (element instanceof PsiNamedElement) {
            name = ((PsiNamedElement) element).getName();
        } else if (element instanceof PsiOpen) {
            name = ((PsiOpen) element).getQualifiedName();
        } else if (element instanceof PsiInclude) {
            name = ((PsiInclude) element).getQualifiedName();
        }

        return name == null ? "" : name;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        if (m_viewElement != null) {
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return m_viewElement.getText();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return m_element instanceof PsiNamedElement ? ((PsiNamedElement) m_element).getName() : "";
                }

                @Nullable
                @Override
                public Icon getIcon(boolean unused) {
                    return PsiIconUtil.getProvidersIcon(m_element, 0);
                }
            };
        }

        if (m_element instanceof NavigationItem) {
            ItemPresentation presentation = ((NavigationItem) m_element).getPresentation();
            if (presentation != null) {
                return presentation;
            }
        }

        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return "Unknown presentation for element " + m_element.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        if (m_element instanceof FileBase) {
            List<TreeElement> treeElements = new ArrayList<>();
            m_element.acceptChildren(new ElementVisitor(treeElements));
            return treeElements.toArray(new TreeElement[0]);
        } else if (m_element instanceof PsiInnerModule) {
            List<TreeElement> treeElements = buildModuleStructure((PsiInnerModule) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        } else if (m_element instanceof PsiFunctor) {
            List<TreeElement> treeElements = buildFunctorStructure((PsiFunctor) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        } else if (m_element instanceof PsiClass) {
            List<TreeElement> treeElements = buildClassStructure((PsiClass) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        } else if (m_element instanceof OclYaccHeader) {
            List<TreeElement> treeElements = buildYaccHeaderStructure((OclYaccHeader) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        } else if (m_element instanceof OclYaccTrailer) {
            List<TreeElement> treeElements = buildYaccTrailerStructure((OclYaccTrailer) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        }

        return EMPTY_ARRAY;
    }

    @NotNull
    private List<TreeElement> buildModuleStructure(PsiInnerModule moduleElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiSignature moduleSignature = moduleElement.getSignature();
        PsiElement rootElement = moduleSignature;
        if (rootElement == null) {
            rootElement = moduleElement.getBody();
        }

        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements));
        }

        // Process body if there is a signature
        if (moduleSignature != null) {
            rootElement = moduleElement.getBody();
            if (rootElement != null) {
                treeElements.add(new StructureModuleImplView(rootElement));
            }
        }

        return treeElements;
    }

    @NotNull
    private List<TreeElement> buildFunctorStructure(PsiFunctor functor) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement binding = functor.getBinding();
        if (binding != null) {
            binding.acceptChildren(new ElementVisitor(treeElements));
        }

        return treeElements;
    }

    @NotNull
    private List<TreeElement> buildClassStructure(@NotNull PsiClass classElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = classElement.getClassBody();
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements));
        }

        return treeElements;
    }

    private List<TreeElement> buildYaccHeaderStructure(OclYaccHeader root) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = ORUtil.findImmediateFirstChildOfType(root, OclYaccTypes.OCAML_LAZY_NODE);
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements));
        }

        return treeElements;
    }

    private List<TreeElement> buildYaccTrailerStructure(OclYaccTrailer root) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = ORUtil.findImmediateFirstChildOfType(root, OclYaccTypes.OCAML_LAZY_NODE);
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
            if (element instanceof PsiStructuredElement) {
                if (((PsiStructuredElement) element).canBeDisplayed()) {
                    if (element instanceof PsiLet) {
                        PsiLet let = (PsiLet) element;
                        if (let.isScopeIdentifier()) {
                            // it's a tuple! add each element of the tuple separately
                            for (PsiElement child : let.getScopeChildren()) {
                                if (child instanceof PsiLowerSymbol) {
                                    m_treeElements.add(new StructureViewElement(child, element));
                                }
                            }
                            return;
                        }
                    }
                    m_treeElements.add(new StructureViewElement(element));
                }
            }
        }
    }

    static class StructureModuleImplView implements StructureViewTreeElement, SortableTreeElement {

        final PsiElement m_rootElement;

        StructureModuleImplView(PsiElement rootElement) {
            m_rootElement = rootElement;
        }

        @NotNull
        @Override
        public ItemPresentation getPresentation() {
            return new ItemPresentation() {
                @Nullable
                @Override
                public String getPresentableText() {
                    return "Implementation";
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean unused) {
                    return Icons.MODULE;
                }
            };
        }

        @NotNull
        @Override
        public TreeElement[] getChildren() {
            List<TreeElement> treeElements = new ArrayList<>();
            m_rootElement.acceptChildren(new ElementVisitor(treeElements));
            return treeElements.toArray(new TreeElement[0]);
        }

        @Override
        public Object getValue() {
            return m_rootElement;
        }

        @Override
        public void navigate(boolean requestFocus) {

        }

        @Override
        public boolean canNavigate() {
            return false;
        }

        @Override
        public boolean canNavigateToSource() {
            return false;
        }

        @NotNull
        @Override
        public String getAlphaSortKey() {
            return "zzzzImplementation";
        }
    }
}
