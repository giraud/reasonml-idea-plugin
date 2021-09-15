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

        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        if (firstChild instanceof PsiFunctorCall) {
            return ((PsiFunctorCall) firstChild).getFunctorName();
        }
        return firstChild == null ? "" : ORUtil.getTextUntilClass(firstChild, PsiConstraints.class);
    }

    @Override
    public @Nullable String[] getResolvedPath() {
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

    @Nullable private PsiUpperSymbol getLatestUpperSymbol(PsiElement root) {
        // Latest element in path
        PsiElement sibling = ORUtil.nextSibling(root);
        PsiUpperSymbol last = sibling instanceof PsiUpperSymbol ? (PsiUpperSymbol) sibling : null;
        while (sibling != null) {
            IElementType elementType = sibling.getNode().getElementType();
            if (elementType == m_types.DOT || elementType == m_types.C_UPPER_SYMBOL) {
                sibling = sibling.getNextSibling();
                if (sibling instanceof PsiUpperSymbol) {
                    last = (PsiUpperSymbol) sibling;
                }
            } else {
                sibling = null;
            }
        }
        return last;
    }

    @Override public @Nullable PsiModule getModule() {
        return null;  // TODO implement method
    }

    @Override
    public boolean useFunctor() {
        PsiElement firstChild = PsiTreeUtil.skipWhitespacesForward(getFirstChild());
        return firstChild instanceof PsiFunctorCall;
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
