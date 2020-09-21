package com.reason.lang.ocamlyacc;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class OclYaccTokenType extends IElementType {
  public OclYaccTokenType(@NotNull String debugName) {
    super(debugName, OclYaccLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String toString() {
    return "OclYaccTokenType." + super.toString();
  }
}
