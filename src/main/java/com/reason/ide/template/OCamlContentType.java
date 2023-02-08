package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

/**
 * Every "*.ml" kind of file is considered to be a part of the OCaml context
 */
public class OCamlContentType extends TemplateContextType {

    protected OCamlContentType() {
        super("OCAML", "Ocaml");
    }

    protected OCamlContentType(@NotNull String name) {
        super("OCAML." + name, name, OCamlContentType.class);
    }

    @Override public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        final String name = templateActionContext.getFile().getName();
        return name.endsWith(".ml") || name.endsWith(".mli");
    }

    // Code-only
    private static final class OCamlExpressionTemplates extends ScopeTemplates {
        public OCamlExpressionTemplates() {
            super("Expression", false);
        }
    }

    // Comments only
    private static final class OCamlCommentTemplates extends ScopeTemplates {
        public OCamlCommentTemplates() {
            super("Comment", true);
        }
    }

    private static class ScopeTemplates extends OCamlContentType {
        private final boolean myOnComment;

        /**
         * @param name      name of the scope, capitalized
         * @param onComment if true, then "isContext" is checking that we are inside a Comment,
         *                  otherwise, "isContext" is checking that we aren't inside a Comment
         */
        protected ScopeTemplates(@NotNull String name, boolean onComment) {
            super(name);
            myOnComment = onComment;
        }

        @Override public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
            if (!super.isInContext(templateActionContext)) {
                return false;
            }

            // RmlContextType - copy of the 6 following lines
            // with RmlLanguage => OclLanguage
            PsiFile file = templateActionContext.getFile();
            int offset = templateActionContext.getStartOffset();
            if (!PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(OclLanguage.INSTANCE)) {
                return false;
            }

            PsiElement element = file.findElementAt(offset);
            return myOnComment == (element instanceof PsiComment);
        }
    }
}
