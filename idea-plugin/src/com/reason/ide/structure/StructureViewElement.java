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
import com.reason.ide.files.DuneFile;
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
    @NotNull
    private final PsiElement m_element;
    @Nullable
    private final PsiElement m_viewElement;
    private final boolean m_navigateToViewElement;

    StructureViewElement(@NotNull PsiElement element) {
        this(null, element, false);
    }

    private StructureViewElement(@Nullable PsiElement viewElement, @NotNull PsiElement element, boolean navigateToViewElement) {
        m_viewElement = viewElement;
        m_element = element;
        m_navigateToViewElement = navigateToViewElement;
    }

    @NotNull
    @Override
    public Object getValue() {
        return m_viewElement == null ? m_element : m_viewElement;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (m_element instanceof NavigationItem) {
            NavigationItem targetElement = (NavigationItem) (m_navigateToViewElement ? m_viewElement : m_element);
            assert targetElement != null;
            targetElement.navigate(requestFocus);
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
        if (m_element instanceof FileBase || m_element instanceof DuneFile) {
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
        } else if (m_element instanceof PsiStanza) {
            List<TreeElement> treeElements = buildStanzaStructure((PsiStanza) m_element);
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        }

        return EMPTY_ARRAY;
    }

    @NotNull
    private List<TreeElement> buildModuleStructure(@NotNull PsiInnerModule moduleElement) {
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
    private List<TreeElement> buildFunctorStructure(@NotNull PsiFunctor functor) {
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

    @NotNull
    private List<TreeElement> buildStanzaStructure(@NotNull PsiStanza stanzaElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = ORUtil.findImmediateFirstChildOfClass(stanzaElement, PsiDuneFields.class);
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements));
        }

        return treeElements;
    }

    @NotNull
    private List<TreeElement> buildYaccHeaderStructure(@NotNull OclYaccHeader root) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = ORUtil.findImmediateFirstChildOfType(root, OclYaccTypes.OCAML_LAZY_NODE);
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements));
        }

        return treeElements;
    }

    @NotNull
    private List<TreeElement> buildYaccTrailerStructure(@NotNull OclYaccTrailer root) {
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
            if (element instanceof PsiStructuredElement && !(element instanceof PsiFakeModule)) {
                if (((PsiStructuredElement) element).canBeDisplayed()) {
                    if (element instanceof PsiLet) {
                        PsiLet let = (PsiLet) element;
                        if (let.isScopeIdentifier()) {
                            // it's a tuple! add each element of the tuple separately.
                            for (PsiElement child : let.getScopeChildren()) {
                                if (child instanceof PsiLowerSymbol) {
                                    m_treeElements.add(new StructureViewElement(child, element, true));
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
                    return "(impl)";
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
