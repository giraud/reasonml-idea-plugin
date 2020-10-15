package com.reason.lang.core.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.impl.PsiInnerModuleImpl;
import com.reason.lang.core.stub.PsiModuleStub;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTypesUtil;
import org.jetbrains.annotations.NotNull;

public class PsiInnerModuleStubElementType extends PsiModuleStubElementType
    implements ORCompositeType {

  public PsiInnerModuleStubElementType(Language language) {
    super("C_MODULE_DECLARATION", language);
  }

  @NotNull
  public PsiInnerModule createPsi(@NotNull final ASTNode node) {
    return new PsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), node);
  }

  @NotNull
  public PsiInnerModule createPsi(@NotNull final PsiModuleStub stub) {
    return new PsiInnerModuleImpl(ORTypesUtil.getInstance(getLanguage()), stub, this);
  }
}
