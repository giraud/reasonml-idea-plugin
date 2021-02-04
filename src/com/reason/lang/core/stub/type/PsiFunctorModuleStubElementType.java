package com.reason.lang.core.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.impl.PsiFunctorImpl;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORTypesUtil;
import org.jetbrains.annotations.NotNull;

public class PsiFunctorModuleStubElementType extends PsiModuleStubElementType {

  public PsiFunctorModuleStubElementType(Language language) {
    super("C_FUNCTOR_DECLARATION", language);
  }

  @NotNull
  public PsiFunctor createPsi(@NotNull final PsiModuleStub stub) {
    return new PsiFunctorImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }

  @NotNull
  public PsiFunctor createPsi(@NotNull ASTNode node) {
    return new PsiFunctorImpl(ORTypesUtil.getInstance(getLanguage()), node);
  }
}
