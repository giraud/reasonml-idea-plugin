package com.reason.lang.core;

import com.intellij.openapi.editor.LogicalPosition;
import org.jetbrains.annotations.NotNull;

public class LogicalHMSignature {

    @NotNull private final LogicalPosition m_logicalPosition;
    @NotNull private final HMSignature m_signature;

    public LogicalHMSignature(@NotNull LogicalPosition position, @NotNull HMSignature signature) {
        m_logicalPosition = position;
        m_signature = signature;
    }

    @NotNull
    public LogicalPosition getLogicalPosition() {
        return m_logicalPosition;
    }

    @NotNull
    public HMSignature getSignature() {
        return m_signature;
    }
}
