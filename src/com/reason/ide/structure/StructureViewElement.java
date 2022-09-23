package com.reason.ide.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final PsiElement m_element;
    private final PsiElement m_viewElement;
    private final int m_level;
    private final boolean m_navigateToViewElement;

    StructureViewElement(@NotNull PsiElement element, int level) {
        this(null, element, false, level);
    }

    private StructureViewElement(@Nullable PsiElement viewElement, @NotNull PsiElement element, boolean navigateToViewElement, int level) {
        m_viewElement = viewElement;
        m_element = element;
        m_navigateToViewElement = navigateToViewElement;
        m_level = level;
    }

    @Override
    public @NotNull Object getValue() {
        return m_viewElement == null ? m_element : m_viewElement;
    }

    int getLevel() {
        return m_level;
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
        return m_element instanceof NavigationItem
                && ((NavigationItem) m_element).canNavigateToSource();
    }

    @Override
    public @NotNull String getAlphaSortKey() {
        String name = null;
        PsiElement element = m_viewElement == null ? m_element : m_viewElement;

        if (element instanceof PsiNamedElement) {
            name = ((PsiNamedElement) element).getName();
        } else if (element instanceof RPsiInclude) {
            name = ((RPsiInclude) element).getIncludePath();
        } else if (element instanceof RPsiOpen) {
            name = ((RPsiOpen) element).getPath();
        }

        return name == null ? "" : name;
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        if (m_viewElement != null) {
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return m_viewElement.getText();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    if (m_element instanceof RPsiLet && ((RPsiLet) m_element).isDeconstruction()) {
                        return "";
                    }
                    return m_element instanceof PsiNamedElement
                            ? ((PsiNamedElement) m_element).getName()
                            : "";
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
            @Override
            public @NotNull String getPresentableText() {
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

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<TreeElement> treeElements = null;

        if (m_element instanceof FileBase || m_element instanceof DuneFile) {
            treeElements = new ArrayList<>();
            m_element.acceptChildren(new ElementVisitor(treeElements, m_level));
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        } else if (m_element instanceof RPsiInnerModule) {
            treeElements = buildModuleStructure((RPsiInnerModule) m_element);
        } else if (m_element instanceof RPsiFunctor) {
            treeElements = buildFunctorStructure((RPsiFunctor) m_element);
        } else if (m_element instanceof RPsiType) {
            treeElements = buildTypeStructure((RPsiType) m_element);
        } else if (m_element instanceof RPsiClass) {
            treeElements = buildClassStructure((RPsiClass) m_element);
        } else if (m_element instanceof RPsiStanza) {
            treeElements = buildStanzaStructure((RPsiStanza) m_element);
        } else if (m_element instanceof RPsiLet) {
            treeElements = buildLetStructure((RPsiLet) m_element);
        }

        if (treeElements != null && !treeElements.isEmpty()) {
            return treeElements.toArray(new TreeElement[0]);
        }

        return EMPTY_ARRAY;
    }

    private @NotNull List<TreeElement> buildModuleStructure(@NotNull RPsiInnerModule moduleElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        RPsiModuleType moduleType = moduleElement.getModuleType();
        PsiElement rootElement = moduleType;
        if (rootElement == null) {
            rootElement = moduleElement.getBody();
        }

        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        // Process body if there is a signature
        if (moduleType != null) {
            rootElement = moduleElement.getBody();
            if (rootElement != null) {
                treeElements.add(new StructureModuleImplView(rootElement));
            }
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildFunctorStructure(@NotNull RPsiFunctor functor) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement binding = functor.getBody();
        if (binding != null) {
            binding.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildTypeStructure(@NotNull RPsiType type) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement binding = type.getBinding();
        if (binding != null) {
            RPsiRecord record = ORUtil.findImmediateFirstChildOfClass(binding, RPsiRecord.class);
            if (record != null) {
                binding.acceptChildren(new ElementVisitor(treeElements, m_level));
            }
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildClassStructure(@NotNull RPsiClass classElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = classElement.getClassBody();
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildStanzaStructure(@NotNull RPsiStanza stanzaElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement =
                ORUtil.findImmediateFirstChildOfClass(stanzaElement, RPsiDuneFields.class);
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    private @Nullable List<TreeElement> buildLetStructure(@NotNull RPsiLet let) {
        List<TreeElement> treeElements = null;

        PsiElement rootElement = let.getBinding();
        if (rootElement != null && let.isFunction()) {
            treeElements = new ArrayList<>();
            rootElement.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    static class ElementVisitor extends PsiElementVisitor {
        private final List<TreeElement> m_treeElements;
        private final int m_elementLevel;

        ElementVisitor(List<TreeElement> elements, int elementLevel) {
            m_treeElements = elements;
            m_elementLevel = elementLevel;
        }

        @Override
        public void visitElement(@NotNull PsiElement element) {
            if (element instanceof RPsiStructuredElement && !(element instanceof RPsiFakeModule)) {
                if (((RPsiStructuredElement) element).canBeDisplayed()) {
                    if (element instanceof RPsiLet) {
                        RPsiLet let = (RPsiLet) element;
                        if (let.isScopeIdentifier()) {
                            // it's a tuple! add each element of the tuple separately.
                            for (PsiElement child : let.getScopeChildren()) {
                                if (child instanceof RPsiLowerSymbol) {
                                    m_treeElements.add(new StructureViewElement(child, element, true, m_elementLevel));
                                }
                            }
                            return;
                        }
                    }
                    m_treeElements.add(new StructureViewElement(element, m_elementLevel));
                }
            } else if (element instanceof RPsiRecord) {
                for (RPsiRecordField field : ((RPsiRecord) element).getFields()) {
                    m_treeElements.add(new StructureViewElement(field, m_elementLevel));
                }
            } else if (element instanceof RPsiScopedExpr && m_elementLevel < 2) {
                List<RPsiStructuredElement> children = ORUtil.findImmediateChildrenOfClass(element, RPsiStructuredElement.class);
                for (RPsiStructuredElement child : children) {
                    if (child.canBeDisplayed()) {
                        m_treeElements.add(new StructureViewElement(child, m_elementLevel));
                    }
                }
            } else if (element instanceof RPsiFunction && m_elementLevel < 2) {
                RPsiFunctionBody body = ((RPsiFunction) element).getBody();
                List<RPsiStructuredElement> children = ORUtil.findImmediateChildrenOfClass(body, RPsiStructuredElement.class);
                for (RPsiStructuredElement child : children) {
                    if (child.canBeDisplayed()) {
                        m_treeElements.add(new StructureViewElement(child, m_elementLevel + 1));
                    }
                }
            }
        }
    }

    static class StructureModuleImplView implements StructureViewTreeElement, SortableTreeElement {
        final PsiElement myRootElement;

        StructureModuleImplView(PsiElement rootElement) {
            myRootElement = rootElement;
        }

        @Override
        public @NotNull ItemPresentation getPresentation() {
            return new ItemPresentation() {
                @Override
                public @NotNull String getPresentableText() {
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

        @Override
        public TreeElement @NotNull [] getChildren() {
            List<TreeElement> treeElements = new ArrayList<>();
            myRootElement.acceptChildren(new ElementVisitor(treeElements, 1));
            return treeElements.toArray(new TreeElement[0]);
        }

        @Override
        public Object getValue() {
            return myRootElement;
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
