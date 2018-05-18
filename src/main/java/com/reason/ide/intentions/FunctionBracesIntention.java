package com.reason.ide.intentions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.impl.RmlElementFactory;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class FunctionBracesIntention extends AbstractBaseIntention<PsiLet> {

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
    Class<PsiLet> getClazz() {
        return PsiLet.class;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiLet parentElement) {
        if (parentElement.isFunction()) {
            PsiLetBinding binding = parentElement.getBinding();
            if (binding != null) {
                PsiElement firstChild = binding.getFirstChild();
                ASTNode childNode = firstChild.getNode();
                return childNode.getElementType() != RmlTypes.INSTANCE.LBRACE;
            }
        }

        return false;
    }

    @Override
    void runInvoke(@NotNull Project project, @NotNull PsiLet parentElement) {
        PsiLetBinding binding = parentElement.getBinding();
        if (binding != null) {
            PsiLet newLet = (PsiLet) RmlElementFactory.createExpression(project, "let x = {\n  " + binding.getText() + ";\n};");
            PsiLetBinding newBinding = newLet == null ? null : newLet.getBinding();
            if (newBinding != null) {
                ASTNode oldBindingNode = binding.getNode();
                parentElement.getNode().replaceChild(oldBindingNode, newBinding.getNode());
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
