package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.ORSignature;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiLetStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiLetImpl extends PsiTokenStub<ORTypes, PsiLetStub> implements PsiLet {

    private ORSignature m_inferredType = ORSignature.EMPTY;

    //region Constructors
    public PsiLetImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiLetImpl(@NotNull ORTypes types, @NotNull PsiLetStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiLetName.class);
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null || nameIdentifier.getNode().getElementType() == m_types.UNDERSCORE ? "" : nameIdentifier.getText();
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    @Override
    @Nullable
    public PsiLetBinding getBinding() {
        return findChildByClass(PsiLetBinding.class);
    }

    @Override
    @Nullable
    public PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @NotNull
    @Override
    public ORSignature getHMSignature() {
        PsiSignature signature = getSignature();
        return signature == null ? ORSignature.EMPTY : signature.asHMSignature();
    }

    @Nullable
    public PsiFunction getFunction() {
        PsiLetBinding binding = getBinding();
        if (binding != null) {
            PsiElement child = binding.getFirstChild();
            if (child instanceof PsiFunction) {
                return (PsiFunction) child;
            }
        }
        return null;
    }

    @Override
    public boolean isObject() {
        return findChildByClass(PsiRecord.class) != null;
    }

    @NotNull
    @Override
    public Collection<PsiRecordField> getObjectFields() {
        return PsiTreeUtil.findChildrenOfType(this, PsiRecordField.class);
    }

    @Override
    public boolean isFunction() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        if (hasInferredType()) {
            return getInferredType().isFunctionSignature();
        } else {
            ORSignature signature = getHMSignature();
            if (signature != ORSignature.EMPTY) {
                return signature.isFunctionSignature();
            }
        }

        if (m_types instanceof RmlTypes) {
            PsiLetBinding binding = findChildByClass(PsiLetBinding.class);
            if (binding == null) {
                return false;
            }
            return binding.getFirstChild() instanceof PsiFunction;
        }

        return PsiTreeUtil.findChildOfType(this, PsiFunction.class) != null;
    }

    private boolean isRecursive() {
        // Find first element after the LET
        PsiElement firstChild = getFirstChild();
        PsiElement sibling = firstChild.getNextSibling();
        if (sibling instanceof PsiWhiteSpace) {
            sibling = sibling.getNextSibling();
        }

        return sibling != null && "rec".equals(sibling.getText());
    }

    //region Inferred type
    @Override
    public ORSignature getInferredType() {
        return m_inferredType;
    }

    @Override
    public void setInferredType(ORSignature inferredType) {
        m_inferredType = inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return m_inferredType != ORSignature.EMPTY;
    }
    //endregion

    @Nullable
    @Override
    public String getQualifiedName() {
        String path;

        PsiModule parent = PsiTreeUtil.getStubOrPsiParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = parent.getQualifiedName();
        } else {
            path = ORUtil.fileNameToModuleName(getContainingFile());
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

                ORSignature signature = hasInferredType() ? getInferredType() : getHMSignature();
                String signatureText = (signature == ORSignature.EMPTY ? "" : ":   " + signature);

                String letName = letValueName.getText();
                if (isFunction()) {
                    return letName + (isRecursive() ? " (rec)" : "") + signatureText;
                }

                return letName + signatureText;
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
    public String toString() {
        return "Let " + getQualifiedName();
    }

    //region Compatibility
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
