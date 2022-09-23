package com.reason.ide.intentions;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class FunctionBracesIntention extends AbstractBaseIntention<RPsiFunction> {

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
    Class<RPsiFunction> getClazz() {
        return RPsiFunction.class;
    }

    @Override
    public boolean isAvailable(@NotNull RPsiFunction parentElement) {
        RPsiFunctionBody body = PsiTreeUtil.findChildOfType(parentElement, RPsiFunctionBody.class);
        if (body != null) {
            PsiElement firstChild = body.getFirstChild();
            if (firstChild instanceof RPsiScopedExpr) {
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
    void runInvoke(@NotNull Project project, @NotNull RPsiFunction oldFunction) {
        RPsiFunctionBody oldBody = oldFunction.getBody();
        if (oldBody != null) {
            String text = oldFunction.getText();
            int bodyOffset = oldBody.getStartOffsetInParent();
            String def = text.substring(0, bodyOffset);
            String body = text.substring(bodyOffset);
            RPsiLet newSyntax =
                    (RPsiLet) ORCodeFactory.createExpression(project, "let x = " + def + "{ " + body + "; };");

            if (newSyntax != null) {
                RPsiFunction newFunction = newSyntax.getFunction();
                if (newFunction != null) {
                    RPsiFunctionBody newBody = newFunction.getBody();
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
