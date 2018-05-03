package com.reason.bs;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.reason.Platform;
import com.reason.ide.facet.BsFacet;
import com.reason.ide.facet.BsFacetConfiguration;

public class ModuleConfiguration {

    public static String getBsbPath(Module module) {
        String result;

        Project project = module.getProject();

        BsFacetConfiguration configuration = BsFacet.getConfiguration(module);
        String bsbLocation = configuration == null ? "" : configuration.location.replace('\\', '/');
        if (bsbLocation.isEmpty()) {
            bsbLocation = "node_modules/bs-platform";
        }

        result = Platform.getBinaryPath(project, bsbLocation + "/lib/bsb.exe");
        if (result == null) {
            result = Platform.getBinaryPath(project, bsbLocation + "/bin/bsb.exe");
        }

        return result;
    }

}
