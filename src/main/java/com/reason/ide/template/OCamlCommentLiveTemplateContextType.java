package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import org.jetbrains.annotations.*;

public class OCamlCommentLiveTemplateContextType extends OCamlBaseLiveTemplateContextType {
    public OCamlCommentLiveTemplateContextType() {
        super("OCaml comment");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        if (super.isInContext(templateActionContext)) {
            return evaluateContext(templateActionContext, true);
        }
        return false;
    }
}
