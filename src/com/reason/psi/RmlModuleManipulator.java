package com.reason.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class RmlModuleManipulator extends AbstractElementManipulator<ReasonMLModule> {
    @Override
    public ReasonMLModule handleContentChange(@NotNull ReasonMLModule reasonMLModule, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        // TODO: why
        return reasonMLModule;
    }
}
