package com.reason.lang.core.psi.impl;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiFunctorBinding;
import com.reason.lang.core.psi.PsiFunctorCall;
import com.reason.lang.core.psi.PsiConstraint;
import com.reason.lang.core.psi.PsiConstraints;
import com.reason.lang.core.psi.PsiFunctorResult;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclQNameFinder;
import com.reason.lang.reason.RmlLanguage;
import com.reason.lang.reason.RmlQNameFinder;
import icons.ORIcons;

import static java.util.Collections.*;

public class PsiFunctorImpl extends PsiTokenStub<ORTypes, PsiModuleStub> implements PsiFunctor {

    //region Constructors
    public PsiFunctorImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiFunctorImpl(@NotNull ORTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
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
        return ORUtil.findImmediateFirstChildOfClass(this, PsiUpperSymbol.class);
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }
    //endregion

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isComponent() {
        return false;
    }

    @Nullable
    @Override
    public String getAlias() {
        return null;
    }

    @NotNull
    @Override
    public String getModuleName() {
        String name = getName();
        return name == null ? "" : name;
    }

    @Nullable
    @Override
    public PsiFunctorCall getFunctorCall() {
        return null;
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

    @NotNull
    @Override
    public Collection<PsiNameIdentifierOwner> getExpressions(@NotNull ExpressionScope eScope) {
        Collection<PsiNameIdentifierOwner> result = emptyList();

        PsiElement returnType = getReturnType();
        if (returnType instanceof PsiFunctorResult) {
            // Resolve return type, and get expressions from there
            PsiFunctorResult functorResult = (PsiFunctorResult) returnType;
            result = new ArrayList<>();

            String name = functorResult.getText();
            QNameFinder qnameFinder = getLanguage() == RmlLanguage.INSTANCE ? RmlQNameFinder.INSTANCE : OclQNameFinder.INSTANCE;
            PsiFinder psiFinder = PsiFinder.getInstance(getProject());
            GlobalSearchScope searchScope = GlobalSearchScope.allScope(getProject());

            Set<String> potentialPaths = qnameFinder.extractPotentialPaths(functorResult);
            for (String potentialPath : potentialPaths) {
                Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(potentialPath + "." + name, true, ORFileType.interfaceOrImplementation, searchScope);
                if (!modulesFromQn.isEmpty()) {
                    PsiModule module = modulesFromQn.iterator().next();
                    return module.getExpressions(eScope);
                }
            }
        }
        else {
            // Get expressions from functor body
            PsiElement body = getBinding();
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
        }

        return result;
    }

    @NotNull
    @Override
    public Collection<PsiModule> getModules() {
        return emptyList();
    }

    @NotNull
    @Override
    public List<PsiLet> getLetExpressions() {
        return emptyList();
    }

    @Nullable
    @Override
    public PsiModule getModuleExpression(@Nullable String name) {
        return null;
    }

    @Nullable
    @Override
    public PsiType getTypeExpression(@Nullable String name) {
        return null;
    }

    @Nullable
    @Override
    public PsiLet getLetExpression(@Nullable String name) {
        return null;
    }

    @Nullable
    @Override
    public PsiVal getValExpression(@Nullable String name) {
        return null;
    }

    @Nullable
    @Override
    public PsiFunctorBinding getBinding() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorBinding.class);
    }

    @NotNull
    @Override
    public Collection<PsiParameter> getParameters() {
        return ORUtil.findImmediateChildrenOfClass(ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class), PsiParameter.class);
    }

    @Nullable
    @Override
    public PsiElement getReturnType() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorResult.class);
    }

    @NotNull
    @Override
    public Collection<PsiConstraint> getConstraints() {
        return ORUtil.findImmediateChildrenOfClass(ORUtil.findImmediateFirstChildOfClass(this, PsiConstraints.class), PsiConstraint.class);
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

    @NotNull
    @Override
    public String toString() {
        return "Functor";
    }
}
