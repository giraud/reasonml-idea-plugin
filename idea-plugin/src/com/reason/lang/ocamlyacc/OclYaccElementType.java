package com.reason.lang.ocamlyacc;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class OclYaccElementType extends IElementType {
  OclYaccElementType(@NotNull String debugName) {
    super(debugName, OclYaccLanguage.INSTANCE);
  }
}
