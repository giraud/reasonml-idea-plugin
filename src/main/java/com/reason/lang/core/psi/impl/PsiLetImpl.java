package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiLetStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiLetImpl extends StubBasedPsiElementBase<PsiLetStub> implements PsiLet {

    private String m_inferredType = "";

    //region Constructors
    public PsiLetImpl(@NotNull ASTNode node) {
        super(node);
    }

    public PsiLetImpl(@NotNull PsiLetStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiVarName.class);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    @Nullable
    public PsiFunBody getFunctionBody() {
        return findChildByClass(PsiFunBody.class);
    }

    @Override
    @Nullable
    public PsiLetBinding getLetBinding() {
        return findChildByClass(PsiLetBinding.class);
    }

    private boolean isFunction() {
        return findChildByClass(PsiFunBody.class) != null;
    }

    private boolean isRecursive() {
        // Find first element after the LET
        PsiElement firstChild = getFirstChild();
        PsiElement sibling = firstChild.getNextSibling();
        if (sibling != null && sibling instanceof PsiWhiteSpace) {
            sibling = sibling.getNextSibling();
        }

        return sibling != null && "rec".equals(sibling.getText());
    }

    //region Inferred type
    @Override
    public void setInferredType(String inferredType) {
        m_inferredType = inferredType.trim();
    }

    @Override
    public String getInferredType() {
        return m_inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return m_inferredType != null && !m_inferredType.isEmpty();
    }
    //endregion

    @Nullable
    @Override
    public String getQualifiedName() {
        String path;

        PsiElement parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = ((PsiModule) parent).getQualifiedName();
        } else {
            path = RmlPsiUtil.fileNameToModuleName(getContainingFile());
        }

        return path + "." + getName();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                PsiElement letValueName = getNameIdentifier();
                if (letValueName == null) {
                    return "_";
                }

                String letName = letValueName.getText();
                if (isFunction()) {
                    return letName + "(..)" + (isRecursive() ? ": rec" : "");
                }

                return letName + (hasInferredType() ? ": " + getInferredType() : "");
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getQualifiedName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return isFunction() ? Icons.FUNCTION : Icons.LET;
            }
        };
    }


    @Override
    public String toString() {
        return "Let(" + getName() + ")";
    }
}
