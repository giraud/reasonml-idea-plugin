package com.reason.ide.intentions;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class FunctionBracesIntention extends AbstractBaseIntention<PsiFunction> {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Add braces to blockless function";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Add braces to blockless function";
    }

    @NotNull
    @Override
    Class<PsiFunction> getClazz() {
        return PsiFunction.class;
    }

    @Override
    public boolean isAvailable(@NotNull PsiFunction parentElement) {
        PsiFunctionBody body = PsiTreeUtil.findChildOfType(parentElement, PsiFunctionBody.class);
        if (body != null) {
            PsiElement firstChild = body.getFirstChild();
            if (firstChild instanceof PsiScopedExpr) {
                firstChild = firstChild.getFirstChild();
                return firstChild != null
                        && firstChild.getNode().getElementType() != RmlTypes.INSTANCE.LBRACE;
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    void runInvoke(@NotNull Project project, @NotNull PsiFunction oldFunction) {
        PsiFunctionBody oldBody = oldFunction.getBody();
        if (oldBody != null) {
            String text = oldFunction.getText();
            int bodyOffset = oldBody.getStartOffsetInParent();
            String def = text.substring(0, bodyOffset);
            String body = text.substring(bodyOffset);
            PsiLet newSyntax =
                    (PsiLet) ORCodeFactory.createExpression(project, "let x = " + def + "{ " + body + "; };");

            if (newSyntax != null) {
                PsiFunction newFunction = newSyntax.getFunction();
                if (newFunction != null) {
                    PsiFunctionBody newBody = newFunction.getBody();
                    if (newBody != null) {
                        oldFunction.getNode().replaceChild(oldBody.getNode(), newBody.getNode());
                    }
                }
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
