package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

/**
 * Every "*.ml" kind of file is considered to be a part of the OCaml context
 */
public class OCamlBaseLiveTemplateContextType extends TemplateContextType {
    protected OCamlBaseLiveTemplateContextType() {
        super("Ocaml");
    }

    public OCamlBaseLiveTemplateContextType(String presentableName) {
        super(presentableName);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        PsiFile file = templateActionContext.getFile();
        return file instanceof OclFile || file instanceof OclInterfaceFile;
    }

    protected boolean evaluateContext(@NotNull TemplateActionContext templateActionContext, boolean inComment) {
        PsiFile file = templateActionContext.getFile();
        int offset = templateActionContext.getStartOffset();
        PsiElement element = file.findElementAt(offset);
        return (element instanceof PsiComment) == inComment;
    }
}
