package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class RPsiObjectField extends RPsiTokenStub<ORLangTypes, RPsiObjectField, PsiObjectFieldStub> implements RPsiField, RPsiLanguageConverter, RPsiQualifiedPathElement, RPsiSignatureElement, StubBasedPsiElement<PsiObjectFieldStub> {
    // region Constructors
    public RPsiObjectField(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiObjectField(@NotNull ORLangTypes types, @NotNull PsiObjectFieldStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    public @Nullable PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public @NotNull String getName() {
        PsiObjectFieldStub stub = getGreenStub();
        if (stub != null) {
            String name = stub.getName();
            return name == null ? "" : name;
        }

        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
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

    @Override
    public @Nullable RPsiSignature getSignature() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiSignature.class);
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang) {
            if (toLang != OclLanguage.INSTANCE) {
                convertedText = new StringBuilder();

                // Convert from OCaml to Reason
                PsiElement nameIdentifier = getNameIdentifier();
                if (nameIdentifier == null) {
                    convertedText.append(getText());
                } else {
                    RPsiFieldValue fieldValue = getValue();
                    PsiElement value;
                    if (fieldValue == null) {
                        value = ORUtil.findImmediateFirstChildOfClass(this, RPsiSignature.class);
                    } else {
                        value = fieldValue.getFirstChild();
                    }

                    String valueAsText = "";
                    if (value instanceof RPsiLanguageConverter) {
                        valueAsText = ((RPsiLanguageConverter) value).asText(toLang);
                    } else if (value != null) {
                        valueAsText = value.getText();
                    }

                    convertedText.append(nameIdentifier.getText()).append(":").append(valueAsText);
                }
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }

    @Nullable
    public RPsiFieldValue getValue() {
        return ORUtil.findImmediateFirstChildOfClass(this, RPsiFieldValue.class);
    }
}
