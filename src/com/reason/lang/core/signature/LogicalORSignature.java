package com.reason.lang.core.signature;

import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.NotNull;

public class LogicalORSignature {

    @NotNull
    private final LogicalPosition m_logicalPosition;
    @NotNull
    private final ORSignature m_signature;

    public LogicalORSignature(@NotNull LogicalPosition position, @NotNull ORSignature signature) {
        m_logicalPosition = position;
        m_signature = signature;
    }

    @NotNull
    public LogicalPosition getLogicalPosition() {
        return m_logicalPosition;
    }

    @NotNull
    public ORSignature getSignature() {
        return m_signature;
    }
}
