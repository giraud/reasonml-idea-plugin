package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;

public interface ModuleName extends NamedElement {
    PsiElement getNameElement();

    String getName();
}
