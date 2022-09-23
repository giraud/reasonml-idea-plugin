package com.reason.lang.core.psi;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A RPsiLet or a RPsiVar are used to declare a variable. They both implement RPsiVar.
 */
public interface RPsiVar {

    boolean isFunction();

    @NotNull
    Collection<RPsiRecordField> getRecordFields();
}
