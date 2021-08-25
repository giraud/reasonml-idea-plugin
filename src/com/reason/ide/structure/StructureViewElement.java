package com.reason.ide.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    @NotNull private final PsiElement m_element;
    @Nullable private final PsiElement m_viewElement;
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
        } else if (element instanceof PsiInclude) {
            name = ((PsiInclude) element).getIncludePath();
        } else if (element instanceof PsiOpen) {
            name = ((PsiOpen) element).getPath();
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
    public @NotNull TreeElement[] getChildren() {
        List<TreeElement> treeElements = null;

        if (m_element instanceof FileBase || m_element instanceof DuneFile) {
            treeElements = new ArrayList<>();
            m_element.acceptChildren(new ElementVisitor(treeElements, m_level));
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        } else if (m_element instanceof PsiInnerModule) {
            treeElements = buildModuleStructure((PsiInnerModule) m_element);
        } else if (m_element instanceof PsiFunctor) {
            treeElements = buildFunctorStructure((PsiFunctor) m_element);
        } else if (m_element instanceof PsiType) {
            treeElements = buildTypeStructure((PsiType) m_element);
        } else if (m_element instanceof PsiClass) {
            treeElements = buildClassStructure((PsiClass) m_element);
        } else if (m_element instanceof PsiStanza) {
            treeElements = buildStanzaStructure((PsiStanza) m_element);
        } else if (m_element instanceof PsiLet) {
            treeElements = buildLetStructure((PsiLet) m_element);
        }

        if (treeElements != null && !treeElements.isEmpty()) {
            return treeElements.toArray(new TreeElement[0]);
        }

        return EMPTY_ARRAY;
    }

    private @NotNull List<TreeElement> buildModuleStructure(@NotNull PsiInnerModule moduleElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiModuleType moduleType = moduleElement.getModuleType();
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

    private @NotNull List<TreeElement> buildFunctorStructure(@NotNull PsiFunctor functor) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement binding = functor.getBinding();
        if (binding != null) {
            binding.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildTypeStructure(@NotNull PsiType type) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement binding = type.getBinding();
        if (binding != null) {
            PsiRecord record = ORUtil.findImmediateFirstChildOfClass(binding, PsiRecord.class);
            if (record != null) {
                binding.acceptChildren(new ElementVisitor(treeElements, m_level));
            }
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildClassStructure(@NotNull PsiClass classElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = classElement.getClassBody();
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildStanzaStructure(@NotNull PsiStanza stanzaElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement =
                ORUtil.findImmediateFirstChildOfClass(stanzaElement, PsiDuneFields.class);
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, m_level));
        }

        return treeElements;
    }

    private @Nullable List<TreeElement> buildLetStructure(PsiLet let) {
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
            if (element instanceof PsiStructuredElement && !(element instanceof PsiFakeModule)) {
                if (((PsiStructuredElement) element).canBeDisplayed()) {
                    if (element instanceof PsiLet) {
                        PsiLet let = (PsiLet) element;
                        if (let.isScopeIdentifier()) {
                            // it's a tuple! add each element of the tuple separately.
                            for (PsiElement child : let.getScopeChildren()) {
                                if (child instanceof PsiLowerIdentifier) {
                                    m_treeElements.add(new StructureViewElement(child, element, true, m_elementLevel));
                                }
                            }
                            return;
                        }
                    }
                    m_treeElements.add(new StructureViewElement(element, m_elementLevel));
                }
            } else if (element instanceof PsiRecord) {
                for (PsiRecordField field : ((PsiRecord) element).getFields()) {
                    m_treeElements.add(new StructureViewElement(field, m_elementLevel));
                }
            } else if (element instanceof PsiScopedExpr && m_elementLevel < 2) {
                List<PsiStructuredElement> children = ORUtil.findImmediateChildrenOfClass(element, PsiStructuredElement.class);
                for (PsiStructuredElement child : children) {
                    if (child.canBeDisplayed()) {
                        m_treeElements.add(new StructureViewElement(child, m_elementLevel));
                    }
                }
            } else if (element instanceof PsiFunction && m_elementLevel < 2) {
                PsiFunctionBody body = ((PsiFunction) element).getBody();
                List<PsiStructuredElement> children = ORUtil.findImmediateChildrenOfClass(body, PsiStructuredElement.class);
                for (PsiStructuredElement child : children) {
                    if (child.canBeDisplayed()) {
                        m_treeElements.add(new StructureViewElement(child, m_elementLevel + 1));
                    }
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
        public @NotNull TreeElement[] getChildren() {
            List<TreeElement> treeElements = new ArrayList<>();
            m_rootElement.acceptChildren(new ElementVisitor(treeElements, 1));
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
