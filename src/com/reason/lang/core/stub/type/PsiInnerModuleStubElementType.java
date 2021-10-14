package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiInnerModuleStubElementType extends PsiModuleStubElementType implements ORCompositeType {
    public PsiInnerModuleStubElementType(@Nullable Language language) {
        super("C_MODULE_DECLARATION", language);
    }

    @NotNull
    public PsiInnerModule createPsi(@NotNull ASTNode node) {
        return new PsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public PsiInnerModule createPsi(@NotNull PsiModuleStub stub) {
        return new PsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }
}
