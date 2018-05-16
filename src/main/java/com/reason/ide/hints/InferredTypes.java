package com.reason.ide.hints;

import com.reason.lang.core.HMSignature;
import com.reason.lang.core.LogicalHMSignature;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface InferredTypes {
    HMSignature getLetType(String name);

    InferredTypes getModuleType(String name);

    @NotNull
    Collection<LogicalHMSignature> listTypesByLines();
}
