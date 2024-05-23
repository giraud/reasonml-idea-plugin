package com.reason.ide.refactor;

import com.intellij.lang.refactoring.*;
import com.intellij.psi.*;
import com.intellij.refactoring.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ORRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        // Not working: getUseScope must return LocalSearchScope
        return element instanceof RPsiLet || element instanceof RPsiInnerModule;
    }

    @Override public @Nullable RefactoringActionHandler getIntroduceVariableHandler() {
        return new ORIntroduceVariableHandler();
    }

    @Override public @Nullable RefactoringActionHandler getIntroduceVariableHandler(PsiElement element) {
        return new ORIntroduceVariableHandler();
    }

    @Override public @Nullable RefactoringActionHandler getIntroduceConstantHandler() {
        return super.getIntroduceConstantHandler();  // TODO implement method override
    }
}
