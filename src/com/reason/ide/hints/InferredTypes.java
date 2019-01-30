package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface InferredTypes {
    @NotNull
    Map<Integer, String> signaturesByLines(Language lang);

    @NotNull
    Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, ORSignature>>> listTypesByIdents();

    @NotNull
    Map<LogicalPosition, String> listOpensByLines();
}
