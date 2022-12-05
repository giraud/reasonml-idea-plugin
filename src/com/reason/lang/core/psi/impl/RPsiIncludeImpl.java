package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.xml.model.gotosymbol.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

public class RPsiIncludeImpl extends RPsiTokenStub<ORLangTypes, RPsiInclude, PsiIncludeStub> implements RPsiInclude {
    // region Constructors
    public RPsiIncludeImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiIncludeImpl(@NotNull ORLangTypes types, @NotNull PsiIncludeStub stub, @NotNull IStubElementType nodeType) {
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
        RPsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorCall.class);
        if (functorCall != null) {
            String path = "";
            if (firstModule != null) {
                path = ORUtil.getTextUntilTokenType(firstModule, (IElementType) myTypes.C_FUNCTOR_CALL);
            }
            return path + functorCall.getName();
        }

        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return firstChild == null ? "" : ORUtil.getTextUntilClass(firstChild, RPsiConstraints.class);
    }

    // deprecate ?
    @Override
    public @Nullable RPsiUpperSymbol getModuleReference() {
        // Latest element in path
        return ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public @Nullable PsiElement resolveModule() {
        RPsiFunctorCall functorCall = ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorCall.class);
        if (functorCall != null) {
            return functorCall.resolveFunctor();
        }

        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return ORUtil.resolveModuleSymbol((RPsiUpperSymbol) firstChild);
    }

    @Override
    public boolean useFunctor() {
        PsiElement firstChild = ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorCall.class);
        return firstChild != null;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new GoToSymbolProvider.BaseNavigationItem(this, getIncludePath(), ORIcons.INCLUDE);
    }

    @Override
    public boolean canBeDisplayed() {
        return !(getParent() instanceof RPsiFunctionBody);
    }
}
