package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.xml.model.gotosymbol.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiOpenImpl extends RPsiTokenStub<ORLangTypes, RPsiOpen, PsiOpenStub> implements RPsiOpen {
    // region Constructors
    public RPsiOpenImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiOpenImpl(@NotNull ORLangTypes types, @NotNull PsiOpenStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    public @NotNull String getPath() {
        PsiOpenStub stub = getGreenStub();
        if (stub != null) {
            String openPath = stub.getOpenPath();
            return openPath == null ? "" : openPath;
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

        // Skip `let` and `open`
        PsiElement firstChild = getFirstChild();
        if (firstChild != null && firstChild.getNode().getElementType() == myTypes.LET) { // `let open` in OCaml
            firstChild = ORUtil.nextSibling(firstChild);
        }
        // Skip force open
        PsiElement child = PsiTreeUtil.skipWhitespacesForward(firstChild);
        if (child != null && child.getNode().getElementType() == myTypes.EXCLAMATION_MARK) {
            child = PsiTreeUtil.skipWhitespacesForward(child);
        }

        return child == null ? "" : ORUtil.getTextUntilTokenType(child, null);
    }

    @Override
    public boolean useFunctor() {
        PsiElement firstChild = ORUtil.findImmediateFirstChildOfClass(this, RPsiFunctorCall.class);
        return firstChild != null;
    }

    @Override
    @Nullable
    public PsiReference getReference() {
        RPsiUpperSymbol moduleSymbol = ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
        return moduleSymbol != null ? moduleSymbol.getReference() : null;
    }

    @Override
    public boolean canBeDisplayed() {
        return !(getParent() instanceof RPsiFunctionBody);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        return new GoToSymbolProvider.BaseNavigationItem(this, getPath(), ORIcons.OPEN);
    }
}
