package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiNamedElement;

public interface PsiQualifiedElement extends PsiNamedElement {
    @NotNull
    String getPath();

    @NotNull
    String getQualifiedName();
}
