package com.reason.ide.refactor;

import com.intellij.lang.refactoring.*;
import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ORRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        // Not working: getUseScope must return LocalSearchScope
        return element instanceof RPsiLet || element instanceof RPsiInnerModule;
    }
}
