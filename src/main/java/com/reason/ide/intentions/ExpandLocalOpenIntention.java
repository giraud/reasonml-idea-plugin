package com.reason.ide.intentions;

import com.intellij.openapi.project.Project;
import com.reason.lang.core.psi.PsiLocalOpen;
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
        // Find start of the module path
        /*
        PsiElement grandParentElement = parentElement.getParent();
        PsiElement newOpen = RmlElementFactory.createExpression(project, "{\n  open X;\n" + parentElement.getText() + ";\n};");
        if (newOpen != null) {
            ASTNode oldOpenNode = parentElement.getNode();
            grandParentElement.getNode().replaceChild(oldOpenNode, newOpen.getNode());
        }
        */
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

}
