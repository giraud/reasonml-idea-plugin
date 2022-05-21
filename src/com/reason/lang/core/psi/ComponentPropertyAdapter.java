package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.impl.PsiAnnotation;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ComponentPropertyAdapter {
    private final String myName;
    private final String myType;
    private final PsiElement myPsiElement;
    private boolean myMandatory;

    public ComponentPropertyAdapter(@NotNull PsiRecordField field, @NotNull List<PsiAnnotation> annotations) {
        myPsiElement = field;
        myName = field.getName();
        PsiSignature signature = field.getSignature();
        myType = signature == null ? "" : signature.asText(RmlLanguage.INSTANCE);
        myMandatory = false; // TODO: hmSignature.isMandatory(0);

        for (PsiAnnotation annotation : annotations) {
            if ("@bs.optional".equals(annotation.getName())) {
                myMandatory = false;
            }
        }
    }

    public ComponentPropertyAdapter(@NotNull PsiParameter parameter) {
        myPsiElement = parameter;
        myName = parameter.getName();
        PsiSignature signature = parameter.getSignature();
        if (signature == null) {
            myType = (parameter.getDefaultValue() == null ? "" : "=" + parameter.getDefaultValue().getText());
        } else {
            myType = signature.asText(ORLanguageProperties.cast(parameter.getLanguage()));
        }
        myMandatory = parameter.getDefaultValue() == null;
    }

    public ComponentPropertyAdapter(@NotNull PsiSignatureItem signatureItem) {
        myPsiElement = signatureItem;
        myName = signatureItem.getName();
        myType = signatureItem.asText(ORLanguageProperties.cast(signatureItem.getLanguage()));
        myMandatory = !signatureItem.isOptional();
    }

    public ComponentPropertyAdapter(@Nullable String name, String type) {
        myPsiElement = null;
        myName = name;
        myType = type;
        myMandatory = false;
    }

    public @Nullable PsiElement getElement() {
        return myPsiElement;
    }

    public @Nullable String getName() {
        return myName;
    }

    public String getType() {
        return myType;
    }

    public boolean isMandatory() {
        return myMandatory;
    }

    @Override
    public @NotNull String toString() {
        return myName + ":" + myType;
    }
}
