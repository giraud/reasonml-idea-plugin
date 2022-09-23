package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

public interface RPsiParameterDeclaration extends PsiNameIdentifierOwner, RPsiQualifiedPathElement, RPsiSignatureElement, RPsiLanguageConverter, StubBasedPsiElement<PsiParameterDeclarationStub> {
    @Nullable
    RPsiDefaultValue getDefaultValue();

    boolean isOptional();

    boolean isNamed();
}
