package com.reason.lang.core.psi;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A PsiLet or a PsiVar are used to declare a variable. They both implement PsiVar.
 */
public interface PsiVar {

    boolean isFunction();

    @NotNull
    Collection<PsiRecordField> getRecordFields();
}
