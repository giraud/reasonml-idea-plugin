package com.reason.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class PsiModuleManipulator extends AbstractElementManipulator<PsiModule> {
    @Override
    public PsiModule handleContentChange(@NotNull PsiModule psiModule, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        // TODO: why
        return psiModule;
    }
}
