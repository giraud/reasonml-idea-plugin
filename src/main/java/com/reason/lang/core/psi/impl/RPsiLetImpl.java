package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

import static java.util.Collections.*;

public class RPsiLetImpl extends RPsiTokenStub<ORLangTypes, RPsiLet, PsiLetStub> implements RPsiLet {
    private RPsiSignature myInferredType;

    // region Constructors
    public RPsiLetImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiLetImpl(@NotNull ORLangTypes types, @NotNull PsiLetStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, RPsiLowerSymbol.class, RPsiScopedExpr.class, RPsiDeconstruction.class, RPsiLiteralExpression.class/*rescript custom operator*/, RPsiUnit.class);
    }

    @Override
    public @Nullable String getName() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        IElementType nameType = nameIdentifier == null ? null : nameIdentifier.getNode().getElementType();
        return nameType == null || nameType == myTypes.UNDERSCORE || nameType == myTypes.C_UNIT
                ? null
                : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        PsiElement id = getNameIdentifier();
        PsiElement newId = ORCodeFactory.createLetName(getProject(), name);
        // deconstruction ???
        if (id != null && newId != null) {
            id.replace(newId);
        }

        return this;
    }
    // endregion

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

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
    public @Nullable RPsiLetBinding getBinding() {
        return findChildByClass(RPsiLetBinding.class);
    }

    @Override
    public boolean isScopeIdentifier() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiDeconstruction.class) != null;
    }

    @Override
    public @NotNull Collection<PsiElement> getScopeChildren() {
        Collection<PsiElement> result = new ArrayList<>();

        PsiElement scope = ORUtil.findImmediateFirstChildOfClass(this, RPsiDeconstruction.class);
        if (scope != null) {
            for (PsiElement element : scope.getChildren()) {
                if (element.getNode().getElementType() != myTypes.COMMA) {
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
    public @Nullable RPsiSignature getSignature() {
        return findChildByClass(RPsiSignature.class);
    }

    @Override
    public boolean isFunction() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunction();
        }

        RPsiSignature inferredType = getInferredType();
        if (inferredType != null) {
            return inferredType.isFunction();
        } else {
            RPsiSignature signature = getSignature();
            if (signature != null) {
                return signature.isFunction();
            }
        }

        RPsiLetBinding binding = findChildByClass(RPsiLetBinding.class);
        return binding != null && binding.getFirstChild() instanceof RPsiFunction;
    }

    public @Nullable RPsiFunction getFunction() {
        RPsiLetBinding binding = getBinding();
        if (binding != null) {
            PsiElement child = binding.getFirstChild();
            if (child instanceof RPsiFunction) {
                return (RPsiFunction) child;
            }
        }
        return null;
    }

    @Override
    public boolean isComponent() {
        PsiLetStub stub = getGreenStub();
        if (stub != null) {
            return stub.isComponent();
        }

        if ("make".equals(getName())) {
            List<RPsiAnnotation> annotations = ORUtil.prevAnnotations(this);
            return annotations.stream().anyMatch(annotation -> {
                String name = annotation.getName();
                return name != null && name.contains("react.component");
            });
        }

        return false;
    }

    @Override
    public boolean isRecord() {
        return findChildByClass(RPsiRecord.class) != null;
    }

    @Override
    public boolean isJsObject() {
        RPsiLetBinding binding = getBinding();
        return binding != null && binding.getFirstChild() instanceof RPsiJsObject;
    }

    @Override
    public @NotNull Collection<RPsiObjectField> getJsObjectFields() {
        RPsiLetBinding binding = getBinding();
        PsiElement firstChild = binding == null ? null : binding.getFirstChild();
        RPsiJsObject jsObject = firstChild instanceof RPsiJsObject ? ((RPsiJsObject) firstChild) : null;
        return jsObject == null ? emptyList() : jsObject.getFields();
    }

    @Override
    public @NotNull Collection<RPsiRecordField> getRecordFields() {
        RPsiLetBinding binding = getBinding();
        PsiElement firstChild = binding == null ? null : binding.getFirstChild();
        RPsiRecord record = firstChild instanceof RPsiRecord ? ((RPsiRecord) firstChild) : null;
        return record == null ? emptyList() : record.getFields();
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
    public @Nullable RPsiSignature getInferredType() {
        return myInferredType;
    }

    @Override
    public void setInferredType(@NotNull RPsiSignature inferredType) {
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
        return nameIdentifier instanceof RPsiDeconstruction;
    }

    @Override
    public boolean isPrivate() {
        RPsiLetAttribute attribute = ORUtil.findImmediateFirstChildOfClass(this, RPsiLetAttribute.class);
        String value = attribute == null ? null : attribute.getValue();
        return value != null && value.equals("private");
    }

    @NotNull
    @Override
    public List<PsiElement> getDeconstructedElements() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier instanceof RPsiDeconstruction) {
            return ((RPsiDeconstruction) nameIdentifier).getDeconstructedElements();
        }
        return emptyList();
    }

    // region RPsiStructuredElement
    @Override
    public boolean canBeDisplayed() {
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier instanceof RPsiUnit) {
            return false;
        }
        if (nameIdentifier != null) {
            return true;
        }

        PsiElement underscore = ORUtil.findImmediateFirstChildOfType(this, myTypes.UNDERSCORE);
        if (underscore != null) {
            return true;
        }

        RPsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(this, RPsiScopedExpr.class);
        return scope != null && !scope.isEmpty();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        final RPsiLet let = this;

        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                PsiElement letValueName = getNameIdentifier();
                if (letValueName == null) {
                    RPsiScopedExpr scope = ORUtil.findImmediateFirstChildOfClass(let, RPsiScopedExpr.class);
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
                RPsiSignature signature = hasInferredType() ? getInferredType() : getSignature();
                return (signature == null ? null : signature.asText(ORLanguageProperties.cast(getLanguage())));
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return isFunction() ? ORIcons.FUNCTION : ORIcons.LET;
            }
        };
    }
    // endregion


    @Override
    public String toString() {
        return "RPsiLet:" + getName();
    }
}
