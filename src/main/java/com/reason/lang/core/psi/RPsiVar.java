package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A RPsiLet or a RPsiVar are used to declare a variable. They both implement RPsiVar.
 */
public interface RPsiVar extends RPsiQualifiedPathElement, PsiNameIdentifierOwner {

    boolean isFunction();

    @NotNull
    Collection<RPsiObjectField> getJsObjectFields();

    @NotNull
    Collection<RPsiRecordField> getRecordFields();

    /**
     * @return true if the name is an underscore `_` or unit `()`
     */
    boolean isAnonymous();
}
