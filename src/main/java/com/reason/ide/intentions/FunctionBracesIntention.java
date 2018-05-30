package com.reason.ide.intentions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.impl.RmlElementFactory;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

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

    @Override
    Class<PsiFunction> getClazz() {
        return PsiFunction.class;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFunction parentElement) {
        PsiElement psiArrow = PsiUtil.nextSiblingWithTokenType(parentElement.getFirstChild(), RmlTypes.INSTANCE.ARROW);
        if (psiArrow != null) {
            PsiElement nextSibling = PsiUtil.nextSibling(psiArrow);
            if (nextSibling instanceof PsiLetBinding || nextSibling instanceof PsiScopedExpr) {
                nextSibling = nextSibling.getFirstChild();
            }
            return nextSibling != null && nextSibling.getNode().getElementType() != RmlTypes.INSTANCE.LBRACE;
        }

        return false;
    }

    @Override
    void runInvoke(@NotNull Project project, @NotNull PsiFunction parentElement) {
        String text = parentElement.getText();
        String[] tokens = text.split("=>");
        PsiElement newSyntax = RmlElementFactory.createExpression(project, tokens[0] + "=> {" + tokens[1] + "; };");

        if (newSyntax != null) {
            parentElement.replace(newSyntax);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
