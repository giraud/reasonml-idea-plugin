package com.reason.ide;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.psi.PsiTypeName;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RmlRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        return element instanceof PsiUpperSymbol || element instanceof PsiTypeName;
    }
}
