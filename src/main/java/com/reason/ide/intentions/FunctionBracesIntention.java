package com.reason.ide.intentions;

import com.intellij.codeInsight.intention.*;
import com.intellij.codeInspection.util.*;
import com.intellij.lang.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class FunctionBracesIntention implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Add braces to blockless function";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Add braces to blockless function";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (file instanceof RmlFile || file instanceof ResFile) {
            RPsiFunction function = getTarget(editor, file);
            RPsiFunctionBody body = function != null ? function.getBody() : null;
            PsiElement bodyChild = body != null ? body.getFirstChild() : null;
            if (bodyChild instanceof RPsiScopedExpr scopedExpr) {
                PsiElement scopeChild = scopedExpr.getFirstChild();
                ORLangTypes types = ORUtil.getTypes(function.getLanguage());
                return scopeChild != null && scopeChild.getNode().getElementType() != types.LBRACE;
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) throws IncorrectOperationException {
        RPsiFunction function = getTarget(editor, file);
        RPsiFunctionBody body = function != null ? function.getBody() : null;
        if (body != null) {
            String text = function.getText();
            int bodyOffset = body.getStartOffsetInParent();
            String def = text.substring(0, bodyOffset);
            String bodyText = text.substring(bodyOffset);
            Language language = function.getLanguage();
            String newExpression = language == RmlLanguage.INSTANCE ? "let x = " + def + "{ " + bodyText + "; };" : "let x = " + def + "{ " + bodyText + " }";
            PsiElement newSyntax = ORCodeFactory.createExpression(project, language, newExpression);
            if (newSyntax instanceof RPsiLet newLetSyntax) {
                RPsiFunction newFunction = newLetSyntax.getFunction();
                RPsiFunctionBody newBody = newFunction != null ? newFunction.getBody() : null;
                if (newBody != null) {
                    WriteCommandAction.runWriteCommandAction(project, null, null, () -> function.getNode().replaceChild(body.getNode(), newBody.getNode()), file);
                }
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Nullable
    private RPsiFunction getTarget(@NotNull Editor editor, @NotNull PsiFile file) {
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        return element != null ? PsiTreeUtil.getParentOfType(element, RPsiFunction.class) : null;
    }
}
