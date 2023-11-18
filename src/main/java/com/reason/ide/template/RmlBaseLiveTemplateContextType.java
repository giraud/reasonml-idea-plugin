package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class RmlBaseLiveTemplateContextType extends TemplateContextType {
    public RmlBaseLiveTemplateContextType() {
        super("ReasonML");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        PsiFile file = context.getFile();
        return file instanceof RmlFile || file instanceof RmlInterfaceFile;
    }
}
