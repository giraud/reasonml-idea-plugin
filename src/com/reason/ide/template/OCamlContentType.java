package com.reason.ide.template;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

/**
 * Every "*.ml" kind of file is considered to be a part of the OCaml context
 */
public class OCamlContentType extends TemplateContextType {

    protected OCamlContentType() {
        super("OCAML", "Ocaml");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        final String name = templateActionContext.getFile().getName();
        return name.endsWith(".ml") || name.endsWith(".mli");
    }
}
