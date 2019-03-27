package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Icons;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.core.stub.PsiLetStub;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
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
        return findChildByClass(PsiLowerSymbol.class);
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
    public boolean isScopeIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class) != null;
    }

    @NotNull
    @Override
    public Collection<PsiElement> getScopeChildren() {
        Collection<PsiElement> result = new ArrayList<>();

        PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
        if (scope != null) {
            for (PsiElement element : scope.getChildren()) {
                if (element.getNode().getElementType() != m_types.COMMA) {
                    result.add(element);
                }
            }
        }

        return result;
    }

    @Override
    @Nullable
    public PsiLetBinding getBinding() {
        return findChildByClass(PsiLetBinding.class);
    }

    @Override
    @Nullable
    public PsiSignature getPsiSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @NotNull
    @Override
    public ORSignature getORSignature() {
        PsiSignature signature = getPsiSignature();
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
            ORSignature signature = getORSignature();
            if (signature != ORSignature.EMPTY) {
                return signature.isFunctionSignature();
            }
        }

        if (m_types instanceof RmlTypes) {
            PsiLetBinding binding = findChildByClass(PsiLetBinding.class);
            return binding != null && binding.getFirstChild() instanceof PsiFunction;
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
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }

    //region PsiStructuredElement
    @Override
    public boolean canBeDisplayed() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier != null) {
            return true;
        }

        PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, PsiScopedExpr.class);
        return scope != null && !scope.isEmpty();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        final PsiLet let = this;

        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                PsiElement letValueName = getNameIdentifier();
                if (letValueName == null) {
                    PsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(let, PsiScopedExpr.class);
                    return scope == null || scope.isEmpty() ? "_" : scope.getText();
                }

                ORSignature signature = hasInferredType() ? getInferredType() : getORSignature();
                String signatureText = (signature == ORSignature.EMPTY ? "" : ":   " + signature.asString(getLanguage()));

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

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return isFunction() ? Icons.FUNCTION : Icons.LET;
            }
        };
    }
    //endregion

    @Nullable
    @Override
    public String toString() {
        return "Let " + getQualifiedName();
    }

    //region Compatibility
    @SuppressWarnings("unused")
    @Nullable
    PsiQualifiedNamedElement getContainer() { // IU-145.2070.6 (2016.1.4)
        return null;
    }
    //endregion
}
