package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import org.jetbrains.annotations.*;

public class OCamlCodeLiveTemplateContextType extends OCamlBaseLiveTemplateContextType {
    public OCamlCodeLiveTemplateContextType() {
        super("OCaml expression");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        if (super.isInContext(templateActionContext)) {
            return evaluateContext(templateActionContext, false);
        }
        return false;
    }
}
