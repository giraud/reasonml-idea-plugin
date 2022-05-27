package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.Collections.*;

public class PsiFunctorImpl extends PsiTokenStub<ORTypes, PsiModule, PsiModuleStub> implements PsiFunctor {
    // region Constructors
    public PsiFunctorImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiFunctorImpl(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
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
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedName
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

    @Override public @Nullable String[] getQualifiedNameAsPath() {
        return ORUtil.getQualifiedNameAsPath(this);
    }

    @Override public @Nullable PsiElement getComponentNavigationElement() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Override
    public @Nullable String getAlias() {
        return null;
    }

    @Override
    public @Nullable PsiUpperSymbol getAliasSymbol() {
        return null;
    }

    @Override
    public @Nullable PsiElement getModuleType() {
        return null;
    }

    @Override
    public @Nullable PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorBinding.class);
    }

    @Override
    public @NotNull String getModuleName() {
        String name = getName();
        return name == null ? "" : name;
    }

    @Override
    public @NotNull Collection<PsiNamedElement> getExpressions(@NotNull ExpressionScope eScope, @Nullable ExpressionFilter filter) {
        Collection<PsiNamedElement> result = emptyList();

        //PsiElement returnType = getReturnType();
        //if (returnType instanceof PsiFunctorResult) {
        //    // Resolve return type, and get expressions from there
        //    PsiFunctorResult functorResult = (PsiFunctorResult) returnType;
        //    result = new ArrayList<>();
        //
        //    String name = functorResult.getText();
        //    Project project = getProject();
        //    PsiFinder psiFinder = project.getService(PsiFinder.class);
        //    QNameFinder qnameFinder = PsiFinder.getQNameFinder(getLanguage());
        //    GlobalSearchScope searchScope = GlobalSearchScope.allScope(project);
        //
        //    Set<String> potentialPaths = qnameFinder.extractPotentialPaths(functorResult);
        //    for (String potentialPath : potentialPaths) {
        //        Set<PsiModule> modulesFromQn =
        //                psiFinder.findModulesFromQn(
        //                        potentialPath + "." + name, true, interfaceOrImplementation);
        //        if (!modulesFromQn.isEmpty()) {
        //            PsiModule module = modulesFromQn.iterator().next();
        //            return module.getExpressions(eScope, filter);
        //        }
        //    }
        //    // nothing found, try without path
        //    Set<PsiModule> modulesFromQn =
        //            psiFinder.findModulesFromQn(name, true, interfaceOrImplementation);
        //    if (!modulesFromQn.isEmpty()) {
        //        PsiModule module = modulesFromQn.iterator().next();
        //        return module.getExpressions(eScope, filter);
        //    }
        //} else {
        //    // Get expressions from functor body
        //    PsiElement body = getBinding();
        //    if (body != null) {
        //        result = new ArrayList<>();
        //        PsiElement element = body.getFirstChild();
        //        while (element != null) {
        //            if (element instanceof PsiNamedElement) {
        //                if (filter == null || filter.accept((PsiNamedElement) element)) {
        //                    result.add((PsiNamedElement) element);
        //                }
        //            }
        //            element = element.getNextSibling();
        //        }
        //    }
        //}

        return result;
    }

    @Override
    public @NotNull Collection<PsiModule> getModules() {
        return emptyList();
    }

    @Override
    public @Nullable PsiModule getModuleExpression(@Nullable String name) {
        return null;
    }

    @Override
    public @Nullable PsiLet getLetExpression(@Nullable String name) {
        if (name != null) {
            ExpressionFilter expressionFilter = element -> element instanceof PsiLet && name.equals(element.getName());
            Collection<PsiNamedElement> expressions = getExpressions(ExpressionScope.all, expressionFilter);
            if (!expressions.isEmpty()) {
                return (PsiLet) expressions.iterator().next();
            }
        }
        return null;
    }

    @Override
    public @NotNull Collection<PsiParameter> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(
                ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class), PsiParameter.class);
    }

    @Override
    public @Nullable PsiFunctorResult getReturnType() {
        PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
        PsiElement element = ORUtil.nextSibling(colon);

        return element instanceof PsiFunctorResult ? (PsiFunctorResult) element : ORUtil.findImmediateFirstChildOfClass(element, PsiFunctorResult.class);
    }

    @Override
    public @NotNull Collection<PsiConstraint> getConstraints() {
        PsiConstraints constraints = ORUtil.findImmediateFirstChildOfClass(this, PsiConstraints.class);
        if (constraints == null) {
            PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
            PsiElement element = ORUtil.nextSibling(colon);
            constraints = element instanceof PsiConstraints ? (PsiConstraints) element : ORUtil.findImmediateFirstChildOfClass(element, PsiConstraints.class);
        }

        return constraints == null ? Collections.emptyList() : ORUtil.findImmediateChildrenOfClass(constraints, PsiConstraint.class);
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
                return ORIcons.FUNCTOR;
            }
        };
    }
}
