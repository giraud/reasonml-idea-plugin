package com.reason.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class RmlModuleNameManipulator extends AbstractElementManipulator<ReasonMLModuleName> {
    @Override
    public ReasonMLModuleName handleContentChange(@NotNull ReasonMLModuleName element, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        // TODO: why
        return element;
    }
}
