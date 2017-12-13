package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.psi.impl.PsiModuleImpl;

public class PsiModuleManipulator extends AbstractElementManipulator<PsiModuleImpl> {
    @Override
    public PsiModuleImpl handleContentChange(@NotNull PsiModuleImpl module, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        // TODO: why
        return module;
    }
}
