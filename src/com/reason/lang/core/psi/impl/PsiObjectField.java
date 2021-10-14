package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class PsiObjectField extends PsiTokenStub<ORTypes, PsiObjectField, PsiObjectFieldStub> implements PsiLanguageConverter, PsiQualifiedPathElement, StubBasedPsiElement<PsiObjectFieldStub> {
    // region Constructors
    public PsiObjectField(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiObjectField(@NotNull ORTypes types, @NotNull PsiObjectFieldStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    @Nullable
    public PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Override public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    //region PsiQualifiedName
    @Override
    public @NotNull String[] getPath() {
        PsiObjectFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiObjectFieldStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Nullable
    public PsiSignature getSignature() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiSignature.class);
    }

    @NotNull
    @Override
    public String asText(@NotNull Language language) {
        if (getLanguage() == language) {
            return getText();
        }

        String convertedText;

        if (language == OclLanguage.INSTANCE) {
            // Convert from Reason to OCaml
            convertedText = getText();
        } else {
            // Convert from OCaml to Reason
            PsiElement nameIdentifier = getNameIdentifier();
            if (nameIdentifier == null) {
                convertedText = getText();
            } else {
                String valueAsText = "";
                PsiElement value = getValue();
                if (value instanceof PsiLanguageConverter) {
                    valueAsText = ((PsiLanguageConverter) value).asText(language);
                } else if (value != null) {
                    valueAsText = value.getText();
                }

                convertedText = "" + nameIdentifier.getText() + ":" + valueAsText;
            }
        }

        return convertedText;
    }

    @Nullable
    public PsiElement getValue() {
        PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
        return colon == null ? null : ORUtil.nextSiblingNode(colon.getNode()).getPsi();
    }

    @NotNull
    @Override
    public String toString() {
        return "ObjectField";
    }
}
