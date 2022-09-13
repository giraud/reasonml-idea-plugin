package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface PsiParameterDeclaration extends PsiNameIdentifierOwner, PsiQualifiedPathElement, PsiSignatureElement, PsiLanguageConverter, StubBasedPsiElement<PsiParameterDeclarationStub> {
    @Nullable
    PsiDefaultValue getDefaultValue();

    boolean isOptional();

    boolean isNamed();
}
