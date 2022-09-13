package com.reason.ide.intentions;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

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
    boolean isAvailable(@NotNull PsiLocalOpen parentElement) {
        return true;
    }

    @Override
    void runInvoke(@NotNull Project project, @NotNull PsiLocalOpen parentElement) {
        // parentElement is the scope: Module.Module «( ... )»
        RmlTypes types = RmlTypes.INSTANCE;
        PsiElement grandParentElement = parentElement.getParent();

        // Extract the module path (and remove path nodes)
        StringBuilder modulePath = new StringBuilder();
        PsiElement sibling = PsiTreeUtil.prevVisibleLeaf(parentElement);
        while (sibling != null
                && (sibling.getNode().getElementType() == types.A_MODULE_NAME
                || sibling.getNode().getElementType() == types.DOT)) {
            ASTNode currentNode = sibling.getNode();
            if ((modulePath.length() > 0) || currentNode.getElementType() != types.DOT) {
                modulePath.insert(0, sibling.getText());
            }
            sibling = PsiTreeUtil.prevVisibleLeaf(sibling);
            grandParentElement.getNode().removeChild(currentNode);
        }

        String text = parentElement.getText();
        PsiElement newOpen = ORCodeFactory.createExpression(project, "{ open " + modulePath + "; " + text.substring(1, text.length() - 1) + "; }");
        if (newOpen != null) {
            grandParentElement.getNode().replaceChild(parentElement.getNode(), newOpen.getNode());
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
