package com.reason.lang.core.psi;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.PsiModuleStub;

public interface PsiInnerModule extends PsiNameIdentifierOwner, PsiModule, StubBasedPsiElement<PsiModuleStub> {
    @Nullable
    PsiSignature getSignature();

    @Nullable
    PsiElement getBody();

    @NotNull
    Collection<PsiInnerModule> getModules();

    @NotNull
    Collection<PsiOpen> getOpenExpressions();

    @NotNull
    Collection<PsiInclude> getIncludeExpressions();

    @NotNull
    Collection<PsiType> getTypeExpressions();

    @Nullable
    PsiExternal getExternalExpression(@NotNull String name);

    @Nullable
    PsiType getTypeExpression(@NotNull String name);

    boolean isComponent();
}
