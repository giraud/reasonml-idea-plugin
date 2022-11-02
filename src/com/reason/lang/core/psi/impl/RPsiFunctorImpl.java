package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.Collections.*;

public class RPsiFunctorImpl extends RPsiTokenStub<ORLangTypes, RPsiModule, PsiModuleStub> implements RPsiFunctor {
    // region Constructors
    public RPsiFunctorImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiFunctorImpl(@NotNull ORLangTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiUpperSymbol.class);
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
    public @Nullable RPsiUpperSymbol getAliasSymbol() {
        return null;
    }

    @Override
    public @Nullable RPsiModuleType getModuleType() {
        return null;
    }

    @Override
    public @Nullable PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorBinding.class);
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
        //if (returnType instanceof RPsiFunctorResult) {
        //    // Resolve return type, and get expressions from there
        //    RPsiFunctorResult functorResult = (RPsiFunctorResult) returnType;
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
        //        Set<RPsiModule> modulesFromQn =
        //                psiFinder.findModulesFromQn(
        //                        potentialPath + "." + name, true, interfaceOrImplementation);
        //        if (!modulesFromQn.isEmpty()) {
        //            RPsiModule module = modulesFromQn.iterator().next();
        //            return module.getExpressions(eScope, filter);
        //        }
        //    }
        //    // nothing found, try without path
        //    Set<RPsiModule> modulesFromQn =
        //            psiFinder.findModulesFromQn(name, true, interfaceOrImplementation);
        //    if (!modulesFromQn.isEmpty()) {
        //        RPsiModule module = modulesFromQn.iterator().next();
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
    public @NotNull Collection<RPsiModule> getModules() {
        return emptyList();
    }

    @Override
    public @Nullable RPsiModule getModuleExpression(@Nullable String name) {
        return null;
    }

    @Override
    public @Nullable RPsiLet getLetExpression(@Nullable String name) {
        if (name != null) {
            ExpressionFilter expressionFilter = element -> element instanceof RPsiLet && name.equals(element.getName());
            Collection<PsiNamedElement> expressions = getExpressions(ExpressionScope.all, expressionFilter);
            if (!expressions.isEmpty()) {
                return (RPsiLet) expressions.iterator().next();
            }
        }
        return null;
    }

    @Override
    public @NotNull List<RPsiParameterDeclaration> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(
                ORUtil.findImmediateFirstChildOfClass(this, RPsiParameters.class), RPsiParameterDeclaration.class);
    }

    @Override
    public @Nullable RPsiFunctorResult getReturnType() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorResult.class);
    }

    @Override
    public @NotNull List<RPsiTypeConstraint> getConstraints() {
        RPsiConstraints constraints = ORUtil.findImmediateFirstChildOfClass(this, RPsiConstraints.class);
        if (constraints == null) {
            PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, myTypes.COLON);
            PsiElement element = ORUtil.nextSibling(colon);
            constraints = element instanceof RPsiConstraints ? (RPsiConstraints) element : ORUtil.findImmediateFirstChildOfClass(element, RPsiConstraints.class);
        }

        return constraints == null ? Collections.emptyList() : ORUtil.findImmediateChildrenOfClass(constraints, RPsiTypeConstraint.class);
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
