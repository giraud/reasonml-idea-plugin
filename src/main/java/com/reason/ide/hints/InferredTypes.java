package com.reason.ide.hints;

import com.reason.lang.core.HMSignature;

public interface InferredTypes {
    HMSignature getLetType(String name);

    InferredTypes getModuleType(String name);
}
