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

public class PsiObjectField extends PsiTokenStub<ORTypes, PsiObjectField, PsiObjectFieldStub> implements PsiLanguageConverter, PsiQualifiedPathElement, StubBasedPsiElement<PsiObjectFieldStub> {
    // region Constructors
    public PsiObjectField(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiObjectField(@NotNull ORTypes types, @NotNull PsiObjectFieldStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    public @Nullable PsiElement getNameIdentifier() {
        return getFirstChild();
    }

    @Override
    public @NotNull String getName() {
        PsiElement nameElement = getNameIdentifier();
        return nameElement == null ? "" : nameElement.getText().replaceAll("\"", "");
    }

    @Override
    public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
    }

    //region PsiQualifiedName
    @Override
    public String @NotNull [] getPath() {
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
                    PsiElement value = getValue();

                    String valueAsText = "";
                    if (value instanceof PsiLanguageConverter) {
                        valueAsText = ((PsiLanguageConverter) value).asText(toLang);
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
    public PsiElement getValue() {
        PsiElement colon = ORUtil.findImmediateFirstChildOfType(this, m_types.COLON);
        return colon == null ? null : ORUtil.nextSiblingNode(colon.getNode()).getPsi();
    }
}
