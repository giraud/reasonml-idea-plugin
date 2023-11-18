package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class ResBaseLiveTemplateContextType extends TemplateContextType {
    public ResBaseLiveTemplateContextType() {
        super("Rescript");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        PsiFile file = context.getFile();
        return file instanceof ResFile || file instanceof ResInterfaceFile;
    }
}
