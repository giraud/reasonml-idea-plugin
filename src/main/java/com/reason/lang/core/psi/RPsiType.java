package com.reason.lang.core.psi;

import com.intellij.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface RPsiType extends PsiNameIdentifierOwner, RPsiQualifiedPathElement, NavigatablePsiElement, RPsiStructuredElement, StubBasedPsiElement<PsiTypeStub> {
    @Nullable
    PsiElement getBinding();

    @NotNull
    Collection<RPsiVariantDeclaration> getVariants();

    boolean isJsObject();

    boolean isRecord();

    @Nullable
    RPsiParameters getParameters();

    @NotNull
    List<RPsiObjectField> getJsObjectFields();

    @NotNull
    List<RPsiRecordField> getRecordFields();

    boolean isAbstract();
}
