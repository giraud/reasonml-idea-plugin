package com.reason.ide.intentions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORElementFactory;
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
        // parentElement is the scope: Module.Module «( ... )»
        RmlTypes types = RmlTypes.INSTANCE;
        PsiElement grandParentElement = parentElement.getParent();

        // Extract the module path (and remove path nodes)
        String modulePath = "";
        PsiElement sibling = PsiTreeUtil.prevVisibleLeaf(parentElement);
        while (sibling != null && (sibling.getNode().getElementType() == types.UIDENT || sibling.getNode().getElementType() == types.DOT)) {
            ASTNode currentNode = sibling.getNode();
            if (!modulePath.isEmpty() || currentNode.getElementType() != types.DOT) {
                modulePath = sibling.getText() + modulePath;
            }
            sibling = PsiTreeUtil.prevVisibleLeaf(sibling);
            grandParentElement.getNode().removeChild(currentNode);
        }

        String text = parentElement.getText();
        PsiElement newOpen = ORElementFactory.createExpression(project, "{ open " + modulePath + "; " + text.substring(1, text.length() - 1) + "; }");
        if (newOpen != null) {
            grandParentElement.getNode().replaceChild(parentElement.getNode(), newOpen.getNode());
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

}
