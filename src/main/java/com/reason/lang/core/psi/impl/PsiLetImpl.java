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
import com.reason.lang.MlTypes;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.PsiLetStub;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class PsiLetImpl extends StubBasedPsiElementBase<PsiLetStub> implements PsiLet {

    @NotNull
    private final MlTypes m_types;

    private HMSignature m_inferredType = HMSignature.EMPTY;

    //region Constructors
    public PsiLetImpl(@NotNull MlTypes types, @NotNull ASTNode node) {
        super(node);
        m_types = types;
    }

    public PsiLetImpl(@NotNull MlTypes types, @NotNull PsiLetStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
        m_types = types;
    }
    //endregion

    //region PsiNamedElement
    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findChildByClass(PsiLowerSymbol.class);
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
    public PsiLetBinding getBinding() {
        return findChildByClass(PsiLetBinding.class);
    }

    @NotNull
    @Override
    public HMSignature getSignature() {
        PsiSignature signature = findChildByClass(PsiSignature.class);
        return signature == null ? HMSignature.EMPTY : signature.asHMSignature();
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
            HMSignature signature = getSignature();
            if (signature != HMSignature.EMPTY) {
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
    public HMSignature getInferredType() {
        return m_inferredType;
    }

    @Override
    public void setInferredType(HMSignature inferredType) {
        m_inferredType = inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return m_inferredType != HMSignature.EMPTY;
    }
    //endregion

    @Nullable
    @Override
    public String getQualifiedName() {
        String path;

        PsiElement parent = PsiTreeUtil.getStubOrPsiParentOfType(this, PsiModule.class);
        if (parent != null) {
            path = ((PsiModule) parent).getQualifiedName();
        } else {
            path = PsiUtil.fileNameToModuleName(getContainingFile());
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

                HMSignature signature = hasInferredType() ? getInferredType() : getSignature();
                String signatureText = (signature == HMSignature.EMPTY ? "" : ":   " + signature);

                String letName = letValueName.getText();
                if (isFunction()) {
                    return letName + (isRecursive() ? " rec" : "") + signatureText;
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


    @Override
    public String toString() {
        return "Let " + getQualifiedName();
    }
}
