package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.signature.ORSignature;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InferredTypes {

  class LogicalPositionSignature {
    String signature;
    int colStart;
    int colEnd;
  }

  @NotNull
  Map<Integer, LogicalPositionSignature> signaturesByLines(@NotNull Language lang);

  @Nullable
  ORSignature getSignatureByPosition(@NotNull LogicalPosition elementPosition);
}
