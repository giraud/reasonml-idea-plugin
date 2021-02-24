package com.reason.lang.core.psi;

import com.intellij.psi.*;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PsiTagStart extends PsiNameIdentifierOwner {
    interface TagProperty {
        @Nullable String getName();

        String getType();

        boolean isMandatory();

        @Nullable PsiElement getElement();
    }

    @NotNull List<PsiTagProperty> getProperties();

    @NotNull List<TagProperty> getUnifiedPropertyList();
}
