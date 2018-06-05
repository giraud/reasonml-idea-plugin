package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.LogicalHMSignature;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public interface InferredTypes {
    HMSignature getLetType(String name);

    InferredTypes getModuleType(String name);

    @NotNull
    Collection<LogicalHMSignature> listTypesByLines();

    @NotNull
    Map<LogicalPosition, String> listOpensByLines();
}
