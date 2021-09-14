package com.reason.ide.template;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.NotNull;

/**
 * Every "*.ml" kind of file is considered to be a part of the OCaml context
 */
public class OCamlContentType extends TemplateContextType {

    protected OCamlContentType() {
        super("OCAML", "Ocaml");
    }

    protected OCamlContentType(String name) {
        super("OCAML."+name, name, OCamlContentType.class);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        final String name = templateActionContext.getFile().getName();
        return name.endsWith(".ml") || name.endsWith(".mli");
    }

    // Code-only
    private static final class OCamlCodeTemplates extends ScopeTemplates {
        public OCamlCodeTemplates() {
            super("Code", false);
        }
    }
    // Comments only
    private static final class OCamlCommentTemplates extends ScopeTemplates {
        public OCamlCommentTemplates() {
            super("Comment", true);
        }
    }

    private static class ScopeTemplates extends OCamlContentType {
        private final boolean comments;

        protected ScopeTemplates(String name, boolean comments) {
            super(name);
            this.comments = comments;
        }

        @Override
        public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
            if(!super.isInContext(templateActionContext)) return false;

            // RmlContextType - copy of the 6 following lines
            // with RmlLanguage => OclLanguage
            PsiFile file = templateActionContext.getFile();
            int offset = templateActionContext.getStartOffset();
            if (!PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(OclLanguage.INSTANCE)) {
                return false;
            }

            PsiElement element = file.findElementAt(offset);
            return this.comments == (element instanceof PsiComment);
        }
    }
}
