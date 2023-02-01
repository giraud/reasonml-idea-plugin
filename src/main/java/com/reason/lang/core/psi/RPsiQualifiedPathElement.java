package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

/**
 * A {@link PsiQualifiedNamedElement} with extra access to the element path as array of strings.
 */
public interface RPsiQualifiedPathElement extends PsiQualifiedNamedElement {
    String @Nullable [] getPath();
}
