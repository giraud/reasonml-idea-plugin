package com.reason.lang.core.psi;

import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A RPsiLet or a RPsiVar are used to declare a variable. They both implement RPsiVar.
 */
public interface RPsiVar {

    boolean isFunction();

    @NotNull
    Collection<RPsiObjectField> getJsObjectFields();

    @NotNull
    Collection<RPsiRecordField> getRecordFields();
}
