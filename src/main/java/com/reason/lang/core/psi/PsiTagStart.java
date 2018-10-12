package com.reason.lang.core.psi;

import java.util.List;

public interface PsiTagStart extends PsiNamedElement {
    interface TagProperty {
        String getName();

        String getType();

        boolean isMandatory();
    }

    List<TagProperty> getUnifiedPropertyList();
}
