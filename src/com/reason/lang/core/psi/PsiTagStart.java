package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface PsiTagStart extends PsiNamedElement {
    interface TagProperty {
        String getName();

        String getType();

        boolean isMandatory();
    }

    @NotNull
    List<PsiTagProperty> getProperties();

    @NotNull
    List<TagProperty> getUnifiedPropertyList();
}
