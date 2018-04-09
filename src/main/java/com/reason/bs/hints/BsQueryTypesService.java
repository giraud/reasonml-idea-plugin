package com.reason.bs.hints;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.lang.core.HMSignature;

import javax.annotation.Nullable;

public interface BsQueryTypesService {

    @Nullable
    BsQueryTypesServiceComponent.InferredTypes types(String filepath);

    @Nullable
    BsQueryTypesServiceComponent.InferredTypes types(VirtualFile filename);

    interface InferredTypes {
        HMSignature getLetType(String name);

        InferredTypes getModuleType(String name);
    }

}
