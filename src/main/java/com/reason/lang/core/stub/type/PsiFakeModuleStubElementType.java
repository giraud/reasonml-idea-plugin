package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class PsiFakeModuleStubElementType extends PsiModuleStubElementType implements ORCompositeType {
    public PsiFakeModuleStubElementType(@NotNull String name, @Nullable Language language) {
        super(name, language);
    }

    @NotNull
    public RPsiFakeModule createPsi(@NotNull final ASTNode node) {
        return new RPsiFakeModule(ORTypesUtil.getInstance(getLanguage()), node);
    }

    @NotNull
    public RPsiFakeModule createPsi(@NotNull final PsiModuleStub stub) {
        return new RPsiFakeModule(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }
}
