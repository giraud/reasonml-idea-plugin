package com.reason.ide.intentions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.RmlElementFactory;
import com.reason.lang.core.psi.PsiLocalOpen;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ExpandLocalOpenIntention extends AbstractBaseIntention<PsiLocalOpen> {

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Expand local open";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "Expand local open";
    }

    @NotNull
    @Override
    Class<PsiLocalOpen> getClazz() {
        return PsiLocalOpen.class;
    }

    @Override
    boolean isAvailable(@NotNull Project project, @NotNull PsiLocalOpen parentElement) {
        return true;
    }

    @Override
    void runInvoke(@NotNull Project project, @NotNull PsiLocalOpen parentElement) {
        PsiElement firstChild = parentElement.getFirstChild();
        PsiElement psiElement = ORUtil.nextSiblingWithTokenType(firstChild, RmlTypes.INSTANCE.SCOPED_EXPR);
        if (psiElement != null) {
            String text = psiElement.getText();
            String modulePath = ORUtil.getTextUntilTokenType(firstChild, RmlTypes.INSTANCE.SCOPED_EXPR);
            PsiElement newOpen = RmlElementFactory.createExpression(project, "{ open " + modulePath.substring(0, modulePath.length() - 1) + "; " + text.substring(1, text.length() - 1) + "; }");
            PsiElement grandParentElement = parentElement.getParent();
            if (newOpen != null) {
                ASTNode oldOpenNode = parentElement.getNode();
                grandParentElement.getNode().replaceChild(oldOpenNode, newOpen.getNode());
            }

        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

}
