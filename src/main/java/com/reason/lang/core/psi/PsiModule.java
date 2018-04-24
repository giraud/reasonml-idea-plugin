package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.ModulePath;
import com.reason.lang.core.stub.ModuleStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface PsiModule extends PsiNamedElement, PsiQualifiedNamedElement, NavigatablePsiElement, PsiStructuredElement, StubBasedPsiElement<ModuleStub> {
    @Nullable
    PsiSignature getSignature();

    @Nullable
    PsiScopedExpr getBody();

    @NotNull
    Collection<PsiOpen> getOpenExpressions();

    @NotNull
    Collection<PsiInclude> getIncludeExpressions();

    @NotNull
    Collection<PsiModule> getModules();

    @NotNull
    Collection<PsiNamedElement> getExpressions();

    @NotNull
    Collection<PsiLet> getLetExpressions();

    @NotNull
    Collection<PsiType> getTypeExpressions();

    @Nullable
    PsiExternal getExternalExpression(@NotNull String name);

    @Nullable
    PsiNamedElement getLetExpression(@NotNull String make);

    @NotNull
    ModulePath getPath();

    boolean isComponent();

    @Nullable
    String getAlias();
}
