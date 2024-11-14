package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class RPsiParameterDeclarationImpl extends RPsiTokenStub<ORLangTypes, RPsiParameterDeclaration, PsiParameterDeclarationStub> implements RPsiParameterDeclaration {
    // region Constructors
    public RPsiParameterDeclarationImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiParameterDeclarationImpl(@NotNull ORLangTypes types, @NotNull PsiParameterDeclarationStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    //region PsiNamedElement
    public @Nullable PsiElement getNameIdentifier() {
        PsiElement parent = getParent();
        PsiElement grandParent = parent == null ? null : parent.getParent();
        if (grandParent instanceof RPsiFunctionCall || grandParent instanceof RPsiFunctorCall) {
            return null;
        }

        PsiElement identifier = getFirstChild();
        IElementType elementType = identifier == null ? null : identifier.getNode().getElementType();
        if (elementType == myTypes.TILDE || elementType == myTypes.LPAREN || elementType == myTypes.QUESTION_MARK) {
            PsiElement nextSibling = identifier.getNextSibling();
            IElementType nextElementType = nextSibling == null ? null : nextSibling.getNode().getElementType();
            identifier = nextElementType == myTypes.LPAREN ? nextSibling.getNextSibling() : nextSibling;
        }

        return identifier;
    }

    @Override
    public @Nullable String getName() {
        PsiParameterDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement identifier = getNameIdentifier();
        if (identifier != null) {
            return identifier.getText();
        }

        PsiElement parent = getParent();
        if (parent instanceof RPsiParameters) {
            List<PsiElement> parameters = ((RPsiParameters) parent).getParametersList();
            int i = 0;
            for (PsiElement parameter : parameters) {
                if (parameter == this) {
                    PsiElement prevSibling = ORUtil.prevSibling(parent);
                    return (prevSibling == null ? "" : prevSibling.getText()) + "[" + i + "]";
                }
                i++;
            }
        }

        return null;
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }
    //endregion

    //region PsiQualifiedName
    @Override
    public @Nullable String[] getPath() {
        PsiParameterDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        PsiQualifiedNamedElement qualifiedParent = PsiTreeUtil.getParentOfType(this, PsiQualifiedNamedElement.class);
        String qName = qualifiedParent == null ? null : qualifiedParent.getQualifiedName();
        return qName == null ? null : qName.split("\\.");
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiParameterDeclarationStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        String name = getName();
        String[] path = getPath();
        return Joiner.join(".", path) + "[" + name + "]";
    }
    //endregion

    @Override
    public @Nullable RPsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, RPsiSignature.class);
    }

    @Override
    public @Nullable RPsiDefaultValue getDefaultValue() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiDefaultValue.class);
    }

    @Override
    public boolean isOptional() {
        return getDefaultValue() != null;
    }

    public boolean isNamed() {
        if (getLanguage() == OclLanguage.INSTANCE) {
            // a signature ?
            return ORUtil.findImmediateFirstChildOfClass(this, RPsiSignature.class) != null;
        }

        PsiElement firstChild = getFirstChild();
        return firstChild != null && firstChild.getNode().getElementType() == myTypes.TILDE;
    }

    @Override public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang && isNamed()) {
            if (fromLang == OclLanguage.INSTANCE) {
                convertedText = new StringBuilder();
                convertedText.append("~").append(getName());
                RPsiSignature signature = getSignature();
                if (signature != null) {
                    convertedText.append(":").append(signature.asText(toLang));
                }
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }
}
