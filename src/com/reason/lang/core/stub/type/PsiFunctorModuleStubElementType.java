package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiFunctorModuleStubElementType extends PsiModuleStubElementType {
    public PsiFunctorModuleStubElementType(@NotNull String name, @NotNull Language language) {
        super(name, language);
    }

    public @NotNull PsiFunctor createPsi(@NotNull PsiModuleStub stub) {
        return new PsiFunctorImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }

    public @NotNull PsiFunctor createPsi(@NotNull ASTNode node) {
        return new PsiFunctorImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }
}
