package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiInnerModuleStubElementType extends PsiModuleStubElementType implements ORCompositeType {
    public PsiInnerModuleStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    public @NotNull RPsiInnerModule createPsi(@NotNull ASTNode node) {
        return new RPsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), node);
    }

    public @NotNull RPsiInnerModule createPsi(@NotNull PsiModuleStub stub) {
        return new RPsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }
}
