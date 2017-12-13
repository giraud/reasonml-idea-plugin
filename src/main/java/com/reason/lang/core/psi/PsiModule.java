package com.reason.lang.core.psi;

import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.reason.lang.core.stub.ModuleStub;

public interface PsiModule extends PsiNamedElement, NavigatablePsiElement, StubBasedPsiElement<ModuleStub> {
    PsiScopedExpr getModuleBody();
}
