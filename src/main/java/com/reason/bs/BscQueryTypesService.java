package com.reason.bs;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Map;

public interface BscQueryTypesService {

    Map<String, String> types(Project project, VirtualFile filename);

}
