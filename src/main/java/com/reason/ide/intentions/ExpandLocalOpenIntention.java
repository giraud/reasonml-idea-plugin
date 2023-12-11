package com.reason.ide.intentions;

import com.intellij.codeInsight.intention.*;
import com.intellij.codeInspection.util.*;
import com.intellij.lang.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class ExpandLocalOpenIntention implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Expand local open";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Expand local open";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (file instanceof RmlFile) {
            PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
            RPsiLocalOpen open = element == null ? null : PsiTreeUtil.getParentOfType(element, RPsiLocalOpen.class);
            return open != null;
        }
        return false;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        RPsiLocalOpen localOpen = element == null ? null : PsiTreeUtil.getParentOfType(element, RPsiLocalOpen.class);
        if (localOpen != null) {
            // localOpen is the scope: Module.Module «( ... )»
            ORLangTypes types = ORUtil.getTypes(localOpen.getLanguage());
            PsiElement grandParentElement = localOpen.getParent();

            // Extract the module path (and remove path nodes)
            StringBuilder modulePath = new StringBuilder();
            PsiElement sibling = PsiTreeUtil.prevVisibleLeaf(localOpen);
            while (sibling != null
                    && (sibling.getNode().getElementType() == types.A_MODULE_NAME
                    || sibling.getNode().getElementType() == types.DOT)) {
                ASTNode currentNode = sibling.getNode();
                if (!modulePath.isEmpty() || currentNode.getElementType() != types.DOT) {
                    modulePath.insert(0, sibling.getText());
                }
                sibling = PsiTreeUtil.prevVisibleLeaf(sibling);
                grandParentElement.getNode().removeChild(currentNode);
            }

            String text = localOpen.getText();
            PsiElement newOpen = ORCodeFactory.createExpression(project, localOpen.getLanguage(), "{ open " + modulePath + "; " + text.substring(1, text.length() - 1) + "; }");
            if (newOpen != null) {
                grandParentElement.getNode().replaceChild(localOpen.getNode(), newOpen.getNode());
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
