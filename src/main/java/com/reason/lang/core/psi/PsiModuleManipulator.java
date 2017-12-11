package com.reason.lang.core.psi;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.core.psi.impl.ModuleImpl;

public class PsiModuleManipulator extends AbstractElementManipulator<ModuleImpl> {
    @Override
    public ModuleImpl handleContentChange(@NotNull ModuleImpl module, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        // TODO: why
        return module;
    }
}
