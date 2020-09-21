package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiFunctionBody;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiParameters;
import com.reason.lang.core.type.ORTypes;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiFunctionImpl extends PsiToken<ORTypes> implements PsiFunction {

  public PsiFunctionImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
    super(types, node);
  }

  @Override
  @NotNull
  public Collection<PsiParameter> getParameters() {
    return ORUtil.findImmediateChildrenOfClass(
        ORUtil.findImmediateFirstChildOfClass(this, PsiParameters.class), PsiParameter.class);
  }

  @Override
  @Nullable
  public PsiFunctionBody getBody() {
    return ORUtil.findImmediateFirstChildOfClass(this, PsiFunctionBody.class);
  }

  @NotNull
  @Override
  public String toString() {
    return "Function";
  }
}
