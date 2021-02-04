package com.reason.lang.core.stub.type;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.reason.lang.core.type.ORCompositeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ORStubElementType<StubT extends StubElement<?>, PsiT extends PsiElement>
    extends IStubElementType<StubT, PsiT> implements ORCompositeType {
  public ORStubElementType(@NotNull String debugName, @Nullable Language language) {
    super(debugName, language);
  }

  public abstract @NotNull PsiElement createPsi(ASTNode node);
}
