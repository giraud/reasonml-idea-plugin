package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface InferredTypes {
    @NotNull
    Map<Integer, String> signaturesByLines(@NotNull Language lang);

    @Nullable
    ORSignature getSignatureByPosition(@NotNull LogicalPosition elementPosition);
}
