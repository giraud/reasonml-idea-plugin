package com.reason.lang.core.stub.type;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.impl.PsiInnerModuleImpl;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTypesUtil;

public class PsiInnerModuleStubElementType extends PsiModuleStubElementType implements ORCompositeType {

    public PsiInnerModuleStubElementType(@NotNull String name, Language language) {
        super(name, language);
    }

    @NotNull
    public PsiInnerModule createPsi(@NotNull final PsiModuleStub stub) {
        return new PsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
    }
}
