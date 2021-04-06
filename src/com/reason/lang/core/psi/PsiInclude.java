package com.reason.lang.core.psi;

public interface PsiInclude extends PsiStructuredElement {
    String getPath();

    boolean useFunctor();
}
