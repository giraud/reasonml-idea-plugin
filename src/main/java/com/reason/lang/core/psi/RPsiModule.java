package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

/**
 * Common interface to file-based modules and inner modules
 */
public interface RPsiModule extends RPsiQualifiedPathElement, RPsiStructuredElement {
    @Nullable
    String getModuleName();

    @Nullable
    PsiElement getBody();

    boolean isInterfaceFile();

    boolean isComponent();
}
