package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.xml.model.gotosymbol.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import icons.*;
import org.jetbrains.annotations.*;

public class PsiOpenImpl extends PsiTokenStub<ORTypes, PsiOpen, PsiOpenStub> implements PsiOpen {
    // region Constructors
    public PsiOpenImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiOpenImpl(@NotNull ORTypes types, @NotNull PsiOpenStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    @NotNull
    public String getPath() {
        PsiOpenStub stub = getGreenStub();
        if (stub != null) {
            return stub.getOpenPath();
        }

        // Skip `let` and `open`
        PsiElement firstChild = getFirstChild();
        if (firstChild != null && firstChild.getNode().getElementType() == m_types.LET) { // `let open` in OCaml
            firstChild = ORUtil.nextSibling(firstChild);
        }
        // Skip force open
        PsiElement child = PsiTreeUtil.skipWhitespacesForward(firstChild);
        if (child != null && child.getNode().getElementType() == m_types.EXCLAMATION_MARK) {
            child = PsiTreeUtil.skipWhitespacesForward(child);
        }

        if (child instanceof PsiFunctorCall) {
            return ((PsiFunctorCall) child).getFunctorName();
        }
        return child == null ? "" : ORUtil.getTextUntilTokenType(child, null);
    }

    @Override
    public boolean useFunctor() {
        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return firstChild instanceof PsiFunctorCall;
    }

    @Override
    public boolean canBeDisplayed() {
        return !(getParent() instanceof PsiFunctionBody);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new GoToSymbolProvider.BaseNavigationItem(this, getPath(), ORIcons.OPEN);
    }

    @Override
    public @Nullable String toString() {
        return "Open " + getPath();
    }
}
