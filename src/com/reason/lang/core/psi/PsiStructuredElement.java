package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;

/**
 * Indicates that the element can be displayed in the structure view
 */
public interface PsiStructuredElement extends PsiElement {
    default boolean canBeDisplayed() {
        return true;
    }
}
