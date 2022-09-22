package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

class CodeInstruction {
    final PsiElement mySource;
    final String[] myValues;
    Set<String> myAlternateValues = null;

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

    public @Nullable String getFirstValue() {
        return myValues == null ? null : myValues[0];
    }

    @Override
    public @NotNull String toString() {
        return "[" + mySource.getClass().getSimpleName() + ":" + Joiner.join("/", myValues) +
                (myAlternateValues == null ? "" : " (" + Joiner.join(", ", myAlternateValues) + ")")
                + "]";
    }
}
