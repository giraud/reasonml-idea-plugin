package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiLiteralExpression;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import com.reason.lang.reason.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class PsiLetImpl extends PsiTokenStub<ORTypes, PsiLet, PsiLetStub> implements PsiLet {
    private PsiSignature myInferredType;

    // region Constructors
    public PsiLetImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiLetImpl(@NotNull ORTypes types, @NotNull PsiLetStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, PsiLowerIdentifier.class, PsiScopedExpr.class, PsiDeconstruction.class, PsiLiteralExpression.class/*rescript custom operator*/, PsiUnit.class);
    }

    @Override
    public @Nullable String getName() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        IElementType nameType = nameIdentifier == null ? null : nameIdentifier.getNode().getElementType();
        return nameType == null || nameType == m_types.UNDERSCORE || nameType == m_types.C_UNIT
                ? null
                : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedName
    @Override
    public String @NotNull [] getPath() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public @Nullable PsiLetBinding getBinding() {
        return findChildByClass(PsiLetBinding.class);
    }

    @Override
    public boolean isScopeIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiDeconstruction.class) != null;
    }

    @Override
    public @NotNull Collection<PsiElement> getScopeChildren() {
        Collection<PsiElement> result = new ArrayList<>();

        PsiElement scope = ORUtil.findImmediateFirstChildOfClass(this, PsiDeconstruction.class);
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
    public @Nullable String getAlias() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.getAlias();
        }

        PsiElement binding = getBinding();
        if (binding != null) {
            return ORUtil.computeAlias(binding.getFirstChild(), getLanguage(), true);
        }

        return null;
    }

    @Override
    public @Nullable PsiElement resolveAlias() {
        PsiElement binding = getBinding();
        PsiLowerSymbol lSymbol = binding == null ? null : ORUtil.findImmediateLastChildOfClass(binding, PsiLowerSymbol.class);
        PsiLowerSymbolReference lReference = lSymbol == null ? null : (PsiLowerSymbolReference) lSymbol.getReference();
        return lReference == null ? null : lReference.resolveInterface();
    }

    @Override
    public @Nullable PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @Override
    public boolean isFunction() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        PsiSignature inferredType = getInferredType();
        if (inferredType != null) {
            return inferredType.isFunction();
        } else {
            PsiSignature signature = getSignature();
            if (signature != null) {
                return signature.isFunction();
            }
        }

        if (m_types instanceof RmlTypes) {
            PsiLetBinding binding = findChildByClass(PsiLetBinding.class);
            return binding != null && binding.getFirstChild() instanceof PsiFunction;
        }

        return PsiTreeUtil.findChildOfType(this, PsiFunction.class) != null;
    }

    public @Nullable PsiFunction getFunction() {
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
    public boolean isRecord() {
        return findChildByClass(PsiRecord.class) != null;
    }

    @Override
    public boolean isJsObject() {
        PsiLetBinding binding = getBinding();
        return binding != null && binding.getFirstChild() instanceof PsiJsObject;
    }

    @Override
    public @NotNull Collection<PsiRecordField> getRecordFields() {
        return PsiTreeUtil.findChildrenOfType(this, PsiRecordField.class);
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

    // region Inferred type
    @Override
    public @Nullable PsiSignature getInferredType() {
        return myInferredType;
    }

    @Override
    public void setInferredType(@NotNull PsiSignature inferredType) {
        myInferredType = inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return myInferredType != null;
    }
    // endregion

    @Override
    public boolean isDeconstruction() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier instanceof PsiDeconstruction;
    }

    @Override
    public boolean isPrivate() {
        PsiLetAttribute attribute = ORUtil.findImmediateFirstChildOfClass(this, PsiLetAttribute.class);
        String value = attribute == null ? null : attribute.getValue();
        return value != null && value.equals("private");
    }

    @NotNull
    @Override
    public List<PsiElement> getDeconstructedElements() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier instanceof PsiDeconstruction) {
            return ((PsiDeconstruction) nameIdentifier).getDeconstructedElements();
        }
        return Collections.emptyList();
    }

    // region PsiStructuredElement
    @Override
    public boolean canBeDisplayed() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier instanceof PsiUnit) {
            return false;
        }
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

                String letName = letValueName.getText();
                if (isFunction()) {
                    return letName + (isRecursive() ? " (rec)" : "");
                }

                return letName;
            }

            @Override
            public @Nullable String getLocationString() {
                PsiSignature signature = hasInferredType() ? getInferredType() : getSignature();
                return (signature == null ? null : signature.asText(ORLanguageProperties.cast(getLanguage())));
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return isFunction() ? ORIcons.FUNCTION : ORIcons.LET;
            }
        };
    }
    // endregion
}
