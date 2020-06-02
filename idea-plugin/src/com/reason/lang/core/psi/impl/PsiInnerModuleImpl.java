package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModuleHelper;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.Collections.emptyList;

public class PsiInnerModuleImpl extends PsiTokenStub<ORTypes, PsiModuleStub> implements PsiInnerModule {

    //region Constructors
    public PsiInnerModuleImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiInnerModuleImpl(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    //region NamedElement
    @Nullable
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
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

    @NotNull
    @Override
    public String getModuleName() {
        String name = getName();
        return name == null ? "" : name;
    }

    @NotNull
    @Override
    public String getPath() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

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
    public Collection<PsiModule> getModules() {
        PsiElement body = getBody();
        return body == null ? emptyList() : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiInnerModule.class);
    }

    @Nullable
    @Override
    public PsiModule getModuleExpression(@Nullable String name) {
        if (name != null) {
            for (PsiModule module : getModules()) {
                if (name.equals(module.getName())) {
                    return module;
                }
            }
        }

        return null;
    }

    @NotNull
    @Override
    public Collection<PsiNameIdentifierOwner> getExpressions(@NotNull ExpressionScope eScope) {
        Collection<PsiNameIdentifierOwner> result = emptyList();

        String alias = getAlias();
        if (alias != null) {
            // Open alias and getExpressions on alias
            Set<PsiModule> modulesbyName = PsiFinder.getInstance(getProject())
                    .findModulesbyName(alias, interfaceOrImplementation, null, GlobalSearchScope.allScope(getProject()));
            if (!modulesbyName.isEmpty()) {
                PsiModule moduleAlias = modulesbyName.iterator().next();
                if (moduleAlias != null) {
                    result = moduleAlias.getExpressions(eScope);
                }
            }
        } else {
            PsiSignature signature = getSignature();
            if (signature == null) {
                PsiElement body = getBody();
                if (body != null) {
                    result = new ArrayList<>();
                    PsiElement element = body.getFirstChild();
                    while (element != null) {
                        if (element instanceof PsiNameIdentifierOwner) {
                            result.add((PsiNameIdentifierOwner) element);
                        }
                        element = element.getNextSibling();
                    }
                }
            } else {
                result = new ArrayList<>();
                PsiElement element = signature.getFirstChild();
                while (element != null) {
                    if (element instanceof PsiNameIdentifierOwner) {
                        result.add((PsiNameIdentifierOwner) element);
                    }
                    element = element.getNextSibling();
                }
            }
        }

        return result;
    }

    @NotNull
    @Override
    public List<PsiLet> getLetExpressions() {
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
    public PsiType getTypeExpression(@Nullable String name) {
        PsiType result = null;

        if (name != null) {
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
        }

        return result;
    }

    @Nullable
    @Override
    public PsiLet getLetExpression(@Nullable String name) {
        if (name == null) {
            return null;
        }

        PsiElement body = getBody();
        if (body != null) {
            List<PsiLet> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiLet.class);
            if (!expressions.isEmpty()) {
                for (PsiLet expression : expressions) {
                    if (name.equals(expression.getName())) {
                        return expression;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public PsiVal getValExpression(@Nullable String name) {
        if (name == null) {
            return null;
        }

        PsiElement body = getBody();
        if (body != null) {
            List<PsiVal> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiVal.class);
            if (!expressions.isEmpty()) {
                for (PsiVal expression : expressions) {
                    if (name.equals(expression.getName())) {
                        return expression;
                    }
                }
            }
        }

        return null;
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
                return ORIcons.MODULE;
            }
        };
    }

    @Override
    public boolean isComponent() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isComponent();
        }

        return ModuleHelper.isComponent(getBody());
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        if (isComponent()) {
            PsiLet make = getLetExpression("make");
            if (make != null) {
                return make;
            }
        }
        return super.getNavigationElement();
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
            return ORUtil.computeAlias(eq.getNextSibling(), getLanguage(), false);
        }

        return null;
    }

    @Override
    public boolean isInterface() {
        return ((FileBase) getContainingFile()).isInterface();
    }

    @Nullable
    @Override
    public String toString() {
        return "Module " + getQualifiedName();
    }
}
