package com.reason.lang.core.stub.type;

import com.intellij.lang.Language;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.impl.PsiFunctorImpl;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTypesUtil;
import org.jetbrains.annotations.NotNull;

public class PsiFunctorModuleStubElementType extends PsiModuleStubElementType
    implements ORCompositeType {

  public PsiFunctorModuleStubElementType(@NotNull String name, Language language) {
    super(name, language);
  }

  @NotNull
  public PsiFunctor createPsi(@NotNull final PsiModuleStub stub) {
    return new PsiFunctorImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }
}
