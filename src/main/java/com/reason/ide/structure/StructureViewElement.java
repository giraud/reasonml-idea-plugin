package com.reason.ide.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class StructureViewElement implements StructureViewTreeElement, SortableTreeElement {
    private final PsiElement myElement;
    private final PsiElement myViewElement;
    private final int myLevel;
    private final boolean myNavigateToViewElement;

    StructureViewElement(@NotNull PsiElement element, int level) {
        this(null, element, false, level);
    }

    private StructureViewElement(@Nullable PsiElement viewElement, @NotNull PsiElement element, boolean navigateToViewElement, int level) {
        myViewElement = viewElement;
        myElement = element;
        myNavigateToViewElement = navigateToViewElement;
        myLevel = level;
    }

    @Override
    public @NotNull Object getValue() {
        return myViewElement == null ? myElement : myViewElement;
    }

    public PsiElement getElement() {
        return myElement;
    }

    int getLevel() {
        return myLevel;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (myElement instanceof NavigationItem) {
            NavigationItem targetElement = (NavigationItem) (myNavigateToViewElement ? myViewElement : myElement);
            assert targetElement != null;
            targetElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return myElement instanceof NavigationItem && ((NavigationItem) myElement).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return myElement instanceof NavigationItem
                && ((NavigationItem) myElement).canNavigateToSource();
    }

    @Override
    public @NotNull String getAlphaSortKey() {
        String name = null;
        PsiElement element = myViewElement == null ? myElement : myViewElement;

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
        if (myViewElement != null) {
            return new ItemPresentation() {
                @Override
                public String getPresentableText() {
                    return myViewElement.getText();
                }

                @Override
                public @Nullable String getLocationString() {
                    if (myElement instanceof RPsiLet && ((RPsiLet) myElement).isDeconstruction()) {
                        return "";
                    }
                    return myElement instanceof PsiNamedElement
                            ? ((PsiNamedElement) myElement).getName()
                            : "";
                }

                @Override
                public @Nullable Icon getIcon(boolean unused) {
                    return PsiIconUtil.getIconFromProviders(myElement, 0);
                }
            };
        }

        if (myElement instanceof NavigationItem) {
            ItemPresentation presentation = ((NavigationItem) myElement).getPresentation();
            if (presentation != null) {
                return presentation;
            }
        }

        return new ItemPresentation() {
            @Override
            public @NotNull String getPresentableText() {
                if (myElement instanceof PsiNamedElement namedElement) {
                    String name = namedElement.getName();
                    if (name != null) {
                        return name;
                    }
                }
                return "Unknown presentation for element " + myElement.getText();
            }

            @Override
            public @NotNull String getLocationString() {
                return "";
            }

            @Override
            public @Nullable Icon getIcon(boolean unused) {
                return PsiIconUtil.getIconFromProviders(myElement, 0);
            }
        };
    }

    @Override
    public @NotNull TreeElement[] getChildren() {
        List<TreeElement> treeElements = null;

        // File
        if (myElement instanceof FileBase || myElement instanceof DuneFile || myElement instanceof MlgFile
                || myElement instanceof MllFile || myElement instanceof MlyFile) {
            treeElements = new ArrayList<>();
            myElement.acceptChildren(new ElementVisitor(treeElements, myLevel));
            if (!treeElements.isEmpty()) {
                return treeElements.toArray(new TreeElement[0]);
            }
        }
        // Lang
        else if (myElement instanceof RPsiInnerModule) {
            treeElements = buildModuleStructure((RPsiInnerModule) myElement);
        } else if (myElement instanceof RPsiFunctor) {
            treeElements = buildFunctorStructure((RPsiFunctor) myElement);
        } else if (myElement instanceof RPsiType) {
            treeElements = buildTypeStructure((RPsiType) myElement);
        } else if (myElement instanceof RPsiClass) {
            treeElements = buildClassStructure((RPsiClass) myElement);
        } else if (myElement instanceof RPsiLet) {
            treeElements = buildLetStructure((RPsiLet) myElement);
        }
        // Dune
        else if (myElement instanceof RPsiDuneStanza) {
            treeElements = buildStanzaStructure((RPsiDuneStanza) myElement);
        }

        if (treeElements != null && !treeElements.isEmpty()) {
            return treeElements.toArray(new TreeElement[0]);
        }

        return EMPTY_ARRAY;
    }

    private @NotNull List<TreeElement> buildModuleStructure(@NotNull RPsiInnerModule moduleElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        RPsiModuleSignature moduleSignature = moduleElement.getModuleSignature();
        if (moduleSignature != null) {
            RPsiUpperSymbol nameIdentifier = moduleSignature.getNameIdentifier();
            if (nameIdentifier != null) {
                // module type of ...
                treeElements.add(new StructureViewElement(moduleSignature, myLevel + 1));
            } else {
                // sig ... end
                moduleSignature.acceptChildren(new ElementVisitor(treeElements, myLevel));
            }
        }

        if (moduleSignature == null) {
            PsiElement body = moduleElement.getBody();
            if (body != null) {
                body.acceptChildren(new ElementVisitor(treeElements, myLevel));
            }
        }

        // Process body if there is a signature
        if (moduleSignature != null) {
            PsiElement rootElement = moduleElement.getBody();
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
            binding.acceptChildren(new ElementVisitor(treeElements, myLevel));
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildTypeStructure(@NotNull RPsiType type) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement binding = type.getBinding();
        if (binding != null) {
            RPsiRecord record = ORUtil.findImmediateFirstChildOfClass(binding, RPsiRecord.class);
            if (record != null) {
                binding.acceptChildren(new ElementVisitor(treeElements, myLevel));
            }
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildClassStructure(@NotNull RPsiClass classElement) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = classElement.getClassBody();
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, myLevel));
        }

        return treeElements;
    }

    private @NotNull List<TreeElement> buildStanzaStructure(@NotNull RPsiDuneStanza stanza) {
        List<TreeElement> treeElements = new ArrayList<>();

        PsiElement rootElement = ORUtil.findImmediateFirstChildOfClass(stanza, RPsiDuneFields.class);
        if (rootElement != null) {
            rootElement.acceptChildren(new ElementVisitor(treeElements, myLevel));
        }

        return treeElements;
    }

    private @Nullable List<TreeElement> buildLetStructure(@NotNull RPsiLet let) {
        List<TreeElement> treeElements = null;

        PsiElement rootElement = let.isFunction() ? let.getFunction() : let.getBinding();
        if (rootElement instanceof RPsiFunction) {
            rootElement = ((RPsiFunction) rootElement).getBody();
        }

        if (rootElement != null) {
            treeElements = new ArrayList<>();
            rootElement.acceptChildren(new ElementVisitor(treeElements, myLevel + 1));
        }

        return treeElements;
    }

    static class ElementVisitor extends PsiElementVisitor {
        private final List<TreeElement> myTreeElements;
        private final int myElementLevel;

        ElementVisitor(List<TreeElement> elements, int elementLevel) {
            myTreeElements = elements;
            myElementLevel = elementLevel;
        }

        @Override
        public void visitElement(@NotNull PsiElement element) {
            switch (element) {
                case RPsiStructuredElement structuredElement when myElementLevel < 3 -> {
                    if (structuredElement.canBeDisplayed()) {
                        if (element instanceof RPsiLet let) {
                            if (let.isScopeIdentifier()) {
                                // it's a tuple! add each element of the tuple separately.
                                for (PsiElement child : let.getScopeChildren()) {
                                    if (child instanceof RPsiLowerSymbol || child instanceof RPsiLowerName) {
                                        myTreeElements.add(new StructureViewElement(child, element, true, myElementLevel));
                                    }
                                }
                                return;
                            }

                            PsiElement letElement = let.isFunction() ? let.getFunction() : let.getBinding();
                            PsiElement letBinding = letElement instanceof RPsiFunction f ? f.getBody() : letElement;
                            PsiElement firstChild = letBinding != null ? letBinding.getFirstChild() : null;
                            if (firstChild instanceof RPsiObject psiObject) {
                                myTreeElements.add(new StructureObjectView(psiObject, let.getName()));
                                return;
                            }

                        }
                        myTreeElements.add(new StructureViewElement(element, myElementLevel));
                    }
                }
                case RPsiRecord record when myElementLevel < 2 -> {
                    for (RPsiRecordField field : record.getFields()) {
                        myTreeElements.add(new StructureViewElement(field, myElementLevel));
                    }
                }
                case RPsiScopedExpr ignored when myElementLevel < 2 -> {
                    List<RPsiStructuredElement> children = ORUtil.findImmediateChildrenOfClass(element, RPsiStructuredElement.class);
                    for (RPsiStructuredElement child : children) {
                        if (child.canBeDisplayed()) {
                            myTreeElements.add(new StructureViewElement(child, myElementLevel));
                        }
                    }
                }
                default -> {
                }
            }
        }
    }


    static abstract class CustomStructureView implements StructureViewTreeElement, SortableTreeElement {
        final PsiElement myRootElement;

        CustomStructureView(PsiElement rootElement) {
            myRootElement = rootElement;
        }

        @Override
        public @NotNull TreeElement[] getChildren() {
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
    }

    static class StructureObjectView extends CustomStructureView {
        private final String myName;

        StructureObjectView(PsiElement rootElement, String name) {
            super(rootElement);
            myName = name;
        }

        @Override
        public @NotNull ItemPresentation getPresentation() {
            return new ItemPresentation() {
                @Override
                public @NotNull String getPresentableText() {
                    return myName;
                }

                @Override
                public @NotNull Icon getIcon(boolean b) {
                    return ORIcons.CLASS;
                }
            };
        }

        @Override
        public @NotNull String getAlphaSortKey() {
            return myName;
        }
    }

    static class StructureModuleImplView extends CustomStructureView {
        StructureModuleImplView(PsiElement rootElement) {
            super(rootElement);
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

        @NotNull
        @Override
        public String getAlphaSortKey() {
            return "zzzImplementation";
        }
    }
}
