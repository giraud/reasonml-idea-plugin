package com.reason.lang.core.psi.reference;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

class CodeInstruction {
    @NotNull PsiElement mySource;
    @Nullable String[] myValues;
    @Nullable Set<String> myAlternateValues = null;

    public CodeInstruction(@NotNull PsiElement source, @Nullable String value, @Nullable Collection<PsiInclude> includes) {
        mySource = source;
        myValues = value == null ? null : new String[]{value};
        if (includes != null && !includes.isEmpty()) {
            myAlternateValues = includes.stream().map(PsiInclude::getIncludePath).collect(Collectors.toSet());
        }
    }

    public CodeInstruction(@NotNull PsiElement source, @Nullable String value) {
        this(source, value, null);
    }

    public CodeInstruction(@NotNull PsiElement source, @NotNull String[] values) {
        mySource = source;
        myValues = values;
    }

    @Nullable public String getFirstValue() {
        return myValues == null ? null : myValues[0];
    }

    @Override
    public String toString() {
        return "[" + mySource.getClass().getSimpleName() + ":" + Joiner.join("/", myValues) +
                (myAlternateValues == null ? "" : " (" + Joiner.join(", ", myAlternateValues) + ")")
                + "]";
    }
}
