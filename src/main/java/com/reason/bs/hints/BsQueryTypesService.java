package com.reason.bs.hints;

import com.intellij.openapi.vfs.VirtualFile;

import javax.annotation.Nullable;

public interface BsQueryTypesService {

    @Nullable
    BsQueryTypesServiceComponent.InferredTypes types(VirtualFile filename);

    interface InferredTypes {
        String getLetType(String name);

        InferredTypes getModuleType(String name);
    }

}
