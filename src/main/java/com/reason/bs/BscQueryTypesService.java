package com.reason.bs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface BscQueryTypesService {

    BscQueryTypesServiceComponent.InferredTypes types(Project project, VirtualFile filename);

    interface InferredTypes {
        String getLetType(String name);

        InferredTypes getModuleType(String name);
    }

}
