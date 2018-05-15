package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.HMSignature;

import java.util.Map;

public interface InferredTypes {
    HMSignature getLetType(String name);

    InferredTypes getModuleType(String name);

    Map<LogicalPosition, HMSignature> listTypesByPositions();
}
