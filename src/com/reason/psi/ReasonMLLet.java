package com.reason.psi;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReasonMLLet extends ReasonMLInferredType {

    @NotNull
    ReasonMLValueName getLetName();

    @Nullable
    ReasonMLFunBody getFunctionBody();

    boolean isFunction();

    ItemPresentation getPresentation();
}
