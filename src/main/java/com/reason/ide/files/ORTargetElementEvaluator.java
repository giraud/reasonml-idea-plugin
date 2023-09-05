package com.reason.ide.files;

import com.intellij.codeInsight.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

/**
 * Allow to target a different element when user navigates to a PsiElement
 * (see <a href="https://github.com/giraud/reasonml-idea-plugin/issues/427">github issue</a>).
 */
public class ORTargetElementEvaluator extends TargetElementEvaluatorEx2 {
    @Override
    public @Nullable PsiElement getGotoDeclarationTarget(@NotNull PsiElement element, @Nullable PsiElement navElement) {
        if (navElement == element && element instanceof RPsiModule module) {
            if (module.isComponent()) {
                PsiElement make = module.getMakeFunction();
                if (make != null) {
                    return make;
                }
            }
        }

        return super.getGotoDeclarationTarget(element, navElement);
    }
}
