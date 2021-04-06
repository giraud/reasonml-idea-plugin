package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ComponentPropertyAdapter {
    private final @Nullable String m_name;
    private final String m_type;
    private final PsiElement m_psiElement;
    private boolean m_mandatory;

    public ComponentPropertyAdapter(@NotNull PsiRecordField field, @NotNull List<PsiAnnotation> annotations) {
        m_psiElement = field;
        m_name = field.getName();
        PsiSignature signature = field.getSignature();
        m_type = signature == null ? "" : signature.asText(RmlLanguage.INSTANCE);
        m_mandatory = false; // TODO: hmSignature.isMandatory(0);

        for (PsiAnnotation annotation : annotations) {
            if ("@bs.optional".equals(annotation.getName())) {
                m_mandatory = false;
            }
        }
    }

    public ComponentPropertyAdapter(@NotNull PsiParameter parameter) {
        m_psiElement = parameter;
        m_name = parameter.getName();
        PsiSignature signature = parameter.getSignature();
        m_type = signature == null ? (parameter.getDefaultValue() == null ? "" : "=" + parameter.getDefaultValue().getText()) : signature.asText(parameter.getLanguage());
        m_mandatory = parameter.getDefaultValue() == null;
    }

    public ComponentPropertyAdapter(@NotNull PsiSignatureItem signatureItem) {
        m_psiElement = signatureItem;
        m_name = signatureItem.getName();
        m_type = signatureItem.asText(signatureItem.getLanguage());
        m_mandatory = !signatureItem.isOptional();
    }

    public ComponentPropertyAdapter(@Nullable String name, String type) {
        m_psiElement = null;
        m_name = name;
        m_type = type;
        m_mandatory = false;
    }

    public @Nullable PsiElement getElement() {
        return m_psiElement;
    }

    public @Nullable String getName() {
        return m_name;
    }

    public String getType() {
        return m_type;
    }

    public boolean isMandatory() {
        return m_mandatory;
    }

    @Override
    public @NotNull String toString() {
        return m_name + ":" + m_type;
    }
}
