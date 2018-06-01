package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.ModulePath;
import com.reason.lang.core.stub.ModuleStub;

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

    @Nullable
    PsiModule getModule(@NotNull String name);

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
