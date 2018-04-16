package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.MlTypes;
import com.reason.lang.core.ModulePath;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.ModuleStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.all;

public class PsiModuleImpl extends StubBasedPsiElementBase<ModuleStub> implements PsiModule {

    private ModulePath m_modulePath;
    private MlTypes m_types;

    //region Constructors
    public PsiModuleImpl(ASTNode node, MlTypes types) {
        super(node);
        m_types = types;
    }

    public PsiModuleImpl(ModuleStub stub, IStubElementType nodeType, MlTypes types) {
        super(stub, nodeType);
        m_types = types;
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

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this; // Use PsiUpperSymbolReference.handleElementRename()
    }
    //endregion

    @Nullable
    public PsiScopedExpr getBody() {
        return findChildByClass(PsiScopedExpr.class);
    }

    @Nullable
    public PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @NotNull
    @Override
    public Collection<PsiOpen> getOpenExpressions() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiOpen.class);
    }

    @NotNull
    @Override
    public Collection<PsiInclude> getIncludeExpressions() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiInclude.class);
    }

    @NotNull
    @Override
    public Collection<PsiModule> getModules() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiModule.class);
    }

    @NotNull
    @Override
    public Collection<PsiNamedElement> getExpressions() {
        Collection<PsiNamedElement> result = Collections.emptyList();

        String alias = getAlias();
        if (alias != null) {
            // Open alias and getExpressions on alias
            PsiModule moduleAlias = PsiFinder.getInstance().findModule(getProject(), alias, interfaceOrImplementation, all);
            if (moduleAlias != null) {
                result = moduleAlias.getExpressions();
            }
        } else {
            PsiScopedExpr body = getBody();
            if (body != null) {
                result = PsiTreeUtil.findChildrenOfAnyType(body, PsiType.class, PsiModule.class, PsiLet.class, PsiExternal.class);
            }
        }

        return result;
    }

    @NotNull
    @Override
    public Collection<PsiLet> getLetExpressions() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiLet.class);
    }

    @Nullable
    @Override
    public PsiExternal getExternalExpression(@NotNull String name) {
        PsiExternal result = null;

        PsiElement body = getClass().isAssignableFrom(PsiFileModuleImpl.class) ? this : getBody();
        if (body != null) {
            List<PsiExternal> externals = PsiTreeUtil.getChildrenOfTypeAsList(body, PsiExternal.class);
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
    public PsiLet getLetExpression(@NotNull String name) {
        PsiLet result = null;

        PsiElement body = getClass().isAssignableFrom(PsiFileModuleImpl.class) ? this : getBody();
        if (body != null) {
            List<PsiLet> expressions = PsiTreeUtil.getChildrenOfTypeAsList(body, PsiLet.class);
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

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.MODULE;
            }
        };
    }

    @NotNull
    @Override
    public ModulePath getPath() {
        // TODO: use stub
        if (m_modulePath == null) {
            List<PsiElement> parents = new ArrayList<>();

            PsiModule parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
            while (parent != null) {
                parents.add(parent);
                parent = PsiTreeUtil.getParentOfType(parent, PsiModule.class);
            }

            Collections.reverse(parents);
            m_modulePath = new ModulePath(parents);
        }

        return m_modulePath;
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    @Nullable
    public String getAlias() {
        ModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getAlias();
        }

        PsiElement eq = findChildByType(m_types.EQ);
        if (eq != null) {
            PsiElement nextSibling = eq.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                nextSibling = nextSibling.getNextSibling();
            }

            if (nextSibling instanceof PsiUpperSymbol) {
                return ((PsiUpperSymbol) nextSibling).getName();
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        ModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return getPath() + "." + getName();
    }

    @Override
    public String toString() {
        return "Module " + getName();
    }
}
