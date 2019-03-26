package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModuleHelper;
import com.reason.lang.core.ModulePath;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.Collections.emptyList;

public class PsiInnerModuleImpl extends PsiTokenStub<ORTypes, PsiModuleStub> implements PsiInnerModule {

    @Nullable
    private ModulePath m_modulePath = null;

    //region Constructors
    public PsiInnerModuleImpl(@NotNull ASTNode node, @NotNull ORTypes types) {
        super(types, node);
    }

    public PsiInnerModuleImpl(@NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType, @NotNull ORTypes types) {
        super(types, stub, nodeType);
    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiUpperSymbol.class);
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Nullable
    public PsiElement getBody() {
        return findChildByClass(PsiScopedExpr.class);
    }

    @Nullable
    public PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @NotNull
    @Override
    public Collection<PsiOpen> getOpenExpressions() {
        PsiElement body = getBody();
        return body == null ? emptyList() : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiOpen.class);
    }

    @NotNull
    @Override
    public Collection<PsiInclude> getIncludeExpressions() {
        PsiElement body = getBody();
        return body == null ? emptyList() : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiInclude.class);
    }

    @NotNull
    @Override
    public Collection<PsiInnerModule> getModules() {
        PsiElement body = getBody();
        return body == null ? emptyList() : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiInnerModule.class);
    }

    @Nullable
    @Override
    public PsiInnerModule getModule(@NotNull String name) {
        Collection<PsiInnerModule> modules = getModules();
        for (PsiInnerModule module : modules) {
            if (name.equals(module.getName())) {
                return module;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Collection<PsiNamedElement> getExpressions() {
        Collection<PsiNamedElement> result = emptyList();

        String alias = getAlias();
        if (alias != null) {
            // Open alias and getExpressions on alias
            PsiModule moduleAlias = PsiFinder.getInstance(getProject()).findModule(alias, interfaceOrImplementation, GlobalSearchScope.allScope(getProject()));
            if (moduleAlias != null) {
                result = moduleAlias.getExpressions();
            }
        } else {
            PsiSignature signature = getSignature();
            if (signature == null) {
                PsiElement body = getBody();
                if (body != null) {
                    result = new ArrayList<>();
                    PsiElement element = body.getFirstChild();
                    while (element != null) {
                        if (element instanceof PsiNamedElement) {
                            result.add((PsiNamedElement) element);
                        }
                        element = element.getNextSibling();
                    }
                }
            } else {
                result = new ArrayList<>();
                PsiElement element = signature.getFirstChild();
                while (element != null) {
                    if (element instanceof PsiNamedElement) {
                        result.add((PsiNamedElement) element);
                    }
                    element = element.getNextSibling();
                }
            }
        }

        return result;
    }

    @NotNull
    @Override
    public Collection<PsiLet> getLetExpressions() {
        PsiElement body = getBody();
        return body == null ? emptyList() : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiLet.class);
    }

    @NotNull
    @Override
    public Collection<PsiType> getTypeExpressions() {
        PsiElement body = getBody();
        return body == null ? emptyList() : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiType.class);
    }

    @Nullable
    @Override
    public PsiExternal getExternalExpression(@NotNull String name) {
        PsiExternal result = null;

        PsiElement body = getBody();
        if (body != null) {
            List<PsiExternal> externals = PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiExternal.class);
            if (!externals.isEmpty()) {
                for (PsiExternal external : externals) {
                    if (name.equals(external.getName())) {
                        result = external;
                        break;
                    }
                }
            }
        }

        return result;
    }

    @Nullable
    @Override
    public PsiType getTypeExpression(@NotNull String name) {
        PsiType result = null;

        PsiElement body = getBody();
        if (body != null) {
            List<PsiType> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiType.class);
            if (!expressions.isEmpty()) {
                for (PsiType expression : expressions) {
                    if (name.equals(expression.getName())) {
                        result = expression;
                        break;
                    }
                }
            }
        }

        return result;
    }

    @Nullable
    @Override
    public PsiLet getLetExpression(@NotNull String name) {
        PsiLet result = null;

        PsiElement body = getBody();
        if (body != null) {
            List<PsiLet> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiLet.class);
            if (!expressions.isEmpty()) {
                for (PsiLet expression : expressions) {
                    if (name.equals(expression.getName())) {
                        result = expression;
                        break;
                    }
                }
            }
        }

        return result;
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @NotNull
            @Override
            public String getLocationString() {
                return getPath().toString();
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.MODULE;
            }
        };
    }

    @NotNull
    private ModulePath getPath() {
        // TODO: use stub
        if (m_modulePath == null) {
            List<PsiElement> parents = new ArrayList<>();

            PsiInnerModule parent = PsiTreeUtil.getStubOrPsiParentOfType(this, PsiInnerModule.class);
            while (parent != null) {
                parents.add(parent);
                parent = PsiTreeUtil.getStubOrPsiParentOfType(parent, PsiInnerModule.class);
            }

            parents.add(getContainingFile());

            Collections.reverse(parents);
            m_modulePath = new ModulePath(parents);
        }

        return m_modulePath;
    }

    @Override
    public boolean isComponent() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isComponent();
        }

        return ModuleHelper.isComponent(getBody());
    }

    @Override
    @Nullable
    public String getAlias() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getAlias();
        }

        PsiElement eq = findChildByType(m_types.EQ);
        if (eq != null) {
            boolean isALias = true;
            StringBuilder aliasName = new StringBuilder();
            PsiElement nextSibling = eq.getNextSibling();
            IElementType elementType = nextSibling == null ? null : nextSibling.getNode().getElementType();
            while (elementType != null && elementType != m_types.SEMI) {
                if (elementType != TokenType.WHITE_SPACE && elementType != m_types.C_UPPER_SYMBOL && elementType != m_types.DOT) {
                    isALias = false;
                    break;
                }

                if (elementType != TokenType.WHITE_SPACE) {
                    aliasName.append(nextSibling.getText());
                }

                nextSibling = nextSibling.getNextSibling();
                elementType = nextSibling == null ? null : nextSibling.getNode().getElementType();
            }

            return isALias ? aliasName.toString() : null;
        }

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    @Nullable
    @Override
    public String toString() {
        return "Module " + getQualifiedName();
    }

    //region Compatibility
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
