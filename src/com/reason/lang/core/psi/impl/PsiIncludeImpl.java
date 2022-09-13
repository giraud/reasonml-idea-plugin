package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.xml.model.gotosymbol.*;
import com.reason.ide.search.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

public class PsiIncludeImpl extends PsiTokenStub<ORTypes, PsiInclude, PsiIncludeStub> implements PsiInclude {
    // region Constructors
    public PsiIncludeImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiIncludeImpl(@NotNull ORTypes types, @NotNull PsiIncludeStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    @Override public String[] getQualifiedPath() {
        PsiIncludeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getIncludePath() {
        PsiIncludeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getIncludePath();
        }


        PsiElement firstModule = ORUtil.findImmediateFirstChildOfType(this, myTypes.A_MODULE_NAME);
        PsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
        if (functorCall != null) {
            String path = "";
            if (firstModule != null) {
                path = ORUtil.getTextUntilTokenType(firstModule, (IElementType) myTypes.C_FUNCTOR_CALL);
            }
            return path + functorCall.getName();
        }

        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return firstChild == null ? "" : ORUtil.getTextUntilClass(firstChild, PsiConstraints.class);
    }

    @Override
    public String @Nullable [] getResolvedPath() {
        PsiIncludeStub stub = getGreenStub();
        if (stub != null) {
            return stub.getResolvedPath();
        }

        // Iterate over previous elements, can't use references here because it needs to work during indexing
        String includePath = getIncludePath();
        PsiFinder psiFinder = getProject().getService(PsiFinder.class);
        PsiQualifiedPathElement resolvedElement = psiFinder.findModuleBack(this, includePath);

        String path = resolvedElement == null ? includePath : resolvedElement.getQualifiedName();
        return path == null ? null : path.split("\\.");
    }

    // deprecate ?
    @Override
    public @Nullable PsiUpperSymbol getModuleReference() {
        // Latest element in path
        return ORUtil.findImmediateLastChildOfClass(this, PsiUpperSymbol.class);
    }

    @Override
    public @Nullable PsiElement resolveModule() {
        PsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
        if (functorCall != null) {
            return functorCall.resolveFunctor();
        }

        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return ORUtil.resolveModuleSymbol((PsiUpperSymbol) firstChild);
    }

    @Override
    public boolean useFunctor() {
        PsiElement firstChild = ORUtil.findImmediateFirstChildOfClass(this, PsiFunctorCall.class);
        return firstChild != null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new GoToSymbolProvider.BaseNavigationItem(this, getIncludePath(), ORIcons.INCLUDE);
    }

    @Override
    public boolean canBeDisplayed() {
        return !(getParent() instanceof PsiFunctionBody);
    }

    @Override
    public @NotNull String toString() {
        return "PsiInclude " + getIncludePath();
    }
}
