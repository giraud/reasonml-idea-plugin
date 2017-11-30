package com.reason.lang.core.psi;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.stub.LetStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiLet extends StubBasedPsiElementBase<LetStub> implements PsiInferredType, PsiNameIdentifierOwner, StubBasedPsiElement<LetStub> {
    private String m_inferredType = "";

    //region Constructors
    public PsiLet(ASTNode node) {
        super(node);
    }

    public PsiLet(LetStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }
    //endregion

    @Override
    public String getName() {
        PsiValueName letName = getLetName();
        return letName == null ? "" : letName.getName();
    }

    @Nullable
    public PsiValueName getLetName() {
        return findChildByClass(PsiValueName.class);
    }

    @Nullable
    public PsiFunBody getFunctionBody() {
        return findChildByClass(PsiFunBody.class);
    }

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
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                PsiValueName letValueName = getLetName();
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
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return isFunction() ? Icons.FUNCTION : Icons.LET;
            }
        };
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getLetName();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public String toString() {
        return "Let(" + getName() + ")";
    }
}
