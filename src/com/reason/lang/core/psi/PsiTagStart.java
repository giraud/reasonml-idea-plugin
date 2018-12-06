package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PsiTagStart extends PsiNamedElement {
    interface TagProperty {
        @Nullable
        String getName();

        String getType();

        boolean isMandatory();
    }

    @NotNull
    List<PsiTagProperty> getProperties();

    @NotNull
    List<TagProperty> getUnifiedPropertyList();
}
