package com.reason.bs.hints;

import com.intellij.openapi.vfs.VirtualFile;

public interface BsQueryTypesService {

    BsQueryTypesServiceComponent.InferredTypes types(VirtualFile filename);

    interface InferredTypes {
        String getLetType(String name);

        InferredTypes getModuleType(String name);
    }

}
