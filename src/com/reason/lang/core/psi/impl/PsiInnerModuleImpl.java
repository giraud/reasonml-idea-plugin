package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static com.reason.lang.core.ORFileType.*;
import static java.util.Collections.*;

public class PsiInnerModuleImpl extends PsiTokenStub<ORTypes, PsiModule, PsiModuleStub> implements PsiInnerModule, PsiNameIdentifierOwner {
    // region Constructors
    public PsiInnerModuleImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiInnerModuleImpl(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region NamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByClass(PsiUpperSymbol.class);
    }

    @Override
    public @Nullable String getName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedPath
    @Override
    public @Nullable String[] getPath() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion


    @Override
    public @NotNull PsiElement getNavigationElement() {
        PsiElement id = getNameIdentifier();
        return id == null ? this : id;
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override public @Nullable String[] getQualifiedNameAsPath() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedNameAsPath();
        }

        return ORUtil.getQualifiedNameAsPath(this);
    }

    @Override
    public @NotNull String getModuleName() {
        String name = getName();
        return name == null ? "" : name;
    }

    @Override
    public boolean isInterface() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isInterface();
        }

        if (((FileBase) getContainingFile()).isInterface()) {
            return true;
        }

        PsiElement psiElement = ORUtil.nextSibling(getFirstChild());
        return psiElement != null && psiElement.getNode().getElementType() == myTypes.TYPE;
    }

    @Override
    public @Nullable PsiFunctorCall getFunctorCall() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
    }

    @Override
    public @Nullable PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, PsiModuleBinding.class, PsiSignature.class);
    }

    @Override
    public @Nullable PsiModuleType getModuleType() {
        PsiElement child = ORUtil.findImmediateFirstChildOfAnyClass(this, PsiModuleType.class, PsiScopedExpr.class);
        if (child instanceof PsiScopedExpr) {
            child = ORUtil.findImmediateFirstChildOfClass(child, PsiModuleType.class);
        }
        return child instanceof PsiModuleType ? (PsiModuleType) child : null;
    }

    @Override
    public @NotNull Collection<PsiModule> getModules() {
        PsiElement body = getBody();
        return body == null
                ? emptyList()
                : PsiTreeUtil.getStubChildrenOfTypeAsList(body, PsiInnerModule.class);
    }

    @Override
    public @Nullable PsiModule getModuleExpression(@Nullable String name) {
        if (name != null) {
            for (PsiModule module : getModules()) {
                if (name.equals(module.getName())) {
                    return module;
                }
            }
        }

        return null;
    }

    @Override
    public @NotNull Collection<PsiNamedElement> getExpressions(@NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter) {
        Collection<PsiNamedElement> result = emptyList();

        Project project = getProject();
        PsiFinder psiFinder = project.getService(PsiFinder.class);

        String alias = getAlias();
        if (alias != null) {
            // Open alias and getExpressions on alias
            Set<PsiModule> modulesByName = psiFinder.findModulesbyName(alias, interfaceOrImplementation, null);
            if (!modulesByName.isEmpty()) {
                PsiModule moduleAlias = modulesByName.iterator().next();
                if (moduleAlias != null) {
                    result = moduleAlias.getExpressions(eScope, filter);
                }
            }
        } else {
            PsiModuleType moduleType = getModuleType();
            if (moduleType == null) {
                PsiElement body = getBody();
                if (body == null) {
                    PsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
                    if (functorCall != null) {
                        result = new ArrayList<>();
                        // Include all expressions from functor
                        QNameFinder qnameFinder = QNameFinderFactory.getQNameFinder(getLanguage());

                        Set<String> potentialPaths = qnameFinder.extractPotentialPaths(functorCall);
                        for (String potentialPath : potentialPaths) {
                            Set<PsiModule> modules = psiFinder.findModulesFromQn(
                                    potentialPath + "." + functorCall.getName(),
                                    true,
                                    interfaceOrImplementation
                            );
                            for (PsiModule module : modules) {
                                result.addAll(module.getExpressions(eScope, filter));
                            }
                        }

                        Set<PsiModule> modules = psiFinder.findModulesFromQn(functorCall.getName(), true, interfaceOrImplementation);
                        for (PsiModule module : modules) {
                            result.addAll(module.getExpressions(eScope, filter));
                        }
                    }
                } else {
                    result = new ArrayList<>();
                    PsiElement element = body.getFirstChild();
                    while (element != null) {
                        if (element instanceof PsiNamedElement
                                && (filter == null || filter.accept((PsiNamedElement) element))) {
                            result.add((PsiNamedElement) element);
                        }
                        element = element.getNextSibling();
                    }
                }
            } else {
                result = new ArrayList<>();
                PsiElement element = moduleType.getFirstChild();
                while (element != null) {
                    if (element instanceof PsiNamedElement
                            && (filter == null || filter.accept((PsiNamedElement) element))) {
                        result.add((PsiNamedElement) element);
                    }
                    element = element.getNextSibling();
                }
            }
        }

        return result;
    }

    @Override
    public @Nullable PsiLet getLetExpression(@Nullable String name) {
        PsiElement body = name == null ? null : getBody();
        if (body != null) {
            ExpressionFilter expressionFilter = element -> element instanceof PsiLet && name.equals(element.getName());
            Collection<PsiNamedElement> expressions = getExpressions(ExpressionScope.all, expressionFilter);
            if (!expressions.isEmpty()) {
                return (PsiLet) expressions.iterator().next();
            }
        }

        return null;
    }

    private boolean isModuleTypeOf() {
        PsiElement nextSibling = ORUtil.nextSibling(getFirstChild());
        PsiElement nextNextSibling = ORUtil.nextSibling(nextSibling);
        return nextSibling != null
                && nextNextSibling != null
                && nextSibling.getNode().getElementType() == myTypes.TYPE
                && nextNextSibling.getNode().getElementType() == myTypes.OF;
    }

    private @Nullable PsiModule findReferencedModuleTypeOf() {
        PsiElement of = ORUtil.findImmediateFirstChildOfType(this, myTypes.OF);

        if (of != null) {
            // find latest module name
            PsiElement module = ORUtil.nextSiblingWithTokenType(of, myTypes.UIDENT);
            PsiElement moduleNextSibling = module == null ? null : module.getNextSibling();
            while (moduleNextSibling != null
                    && moduleNextSibling.getNode().getElementType() == myTypes.DOT) {
                PsiElement element = moduleNextSibling.getNextSibling();
                if (element != null && element.getNode().getElementType() == myTypes.UIDENT) {
                    module = element;
                    moduleNextSibling = module.getNextSibling();
                } else {
                    moduleNextSibling = null;
                }
            }

            if (module != null) {
                PsiReference reference = module.getReference();
                PsiElement resolvedElement = reference == null ? null : reference.resolve();
                if (resolvedElement instanceof PsiModule) {
                    return (PsiModule) resolvedElement;
                }
            }
        }

        return null;
    }

    public ItemPresentation getPresentation() {
        boolean isModuleTypeOf = isModuleTypeOf();
        PsiModule referencedModuleType = isModuleTypeOf ? findReferencedModuleTypeOf() : null;

        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                if (isModuleTypeOf) {
                    if (referencedModuleType == null) {
                        PsiElement of = ORUtil.findImmediateFirstChildOfType(PsiInnerModuleImpl.this, myTypes.OF);
                        assert of != null;
                        return getText().substring(of.getStartOffsetInParent() + 3);
                    }
                    return referencedModuleType.getName();
                }
                return getName();
            }

            @Override
            public @NotNull String getLocationString() {
                return referencedModuleType == null
                        ? ""
                        : referencedModuleType.getContainingFile().getName();
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return isInterface()
                        ? ORIcons.MODULE_TYPE
                        : (isInterface() ? ORIcons.INNER_MODULE_INTF : ORIcons.INNER_MODULE);
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

    @Override
    public boolean isFunctorCall() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunctorCall();
        }

        return ORUtil.findImmediateFirstChildOfType(this, myTypes.C_FUNCTOR_CALL) != null;
    }

    @Override
    public @Nullable PsiElement getComponentNavigationElement() {
        if (isComponent()) {
            PsiElement make = ORUtil.findImmediateNamedChildOfClass(getBody(), PsiLet.class, "make");
            if (make == null) {
                make = ORUtil.findImmediateNamedChildOfClass(getBody(), PsiExternal.class, "make");
            }
            return make;
        }
        return null;
    }

    public @Nullable PsiUpperSymbol getAliasSymbol() {
        PsiModuleBinding binding = ORUtil.findImmediateFirstChildOfClass(this, PsiModuleBinding.class);
        return binding == null ? null : ORUtil.findImmediateLastChildOfClass(binding, PsiUpperSymbol.class);
    }

    @Override
    public @Nullable String getAlias() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getAlias();
        }

        PsiModuleBinding binding = ORUtil.findImmediateFirstChildOfClass(this, PsiModuleBinding.class);
        if (binding != null) {
            return ORUtil.computeAlias(binding.getFirstChild(), getLanguage(), false);
        }

        return null;
    }

    @Override public String toString() {
        return "PsiModule:" + getModuleName();
    }
}
