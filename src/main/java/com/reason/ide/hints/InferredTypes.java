package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.LogicalHMSignature;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public interface InferredTypes {
    @NotNull
    Collection<LogicalHMSignature> listTypesByLines();

    @NotNull
    Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, HMSignature>>> listTypesByIdents();

    @NotNull
    Map<LogicalPosition, String> listOpensByLines();
}
