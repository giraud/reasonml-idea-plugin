package com.reason.psi;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;

public interface ReasonMLLet extends ReasonMLInferredType {

    @NotNull
    ReasonMLValueName getLetName();

    ItemPresentation getPresentation();

    boolean isFunction();
}
