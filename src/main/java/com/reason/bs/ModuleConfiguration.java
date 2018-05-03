package com.reason.bs;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.ide.facet.BsFacet;
import com.reason.ide.facet.BsFacetConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleConfiguration {

    private final Module m_module;

    ModuleConfiguration(@NotNull VirtualFile baseDir, @NotNull Project project) {
        m_module = ModuleUtil.findModuleForFile(baseDir, project);
    }

    @Nullable
    public String getBsbPath() {
        String result;

        String bsLocation = getBsPlatformLocation();
        Project project = m_module.getProject();

        result = Platform.getBinaryPath(project, bsLocation + "/lib/bsb.exe");
        if (result == null) {
            result = Platform.getBinaryPath(project, bsLocation + "/bin/bsb.exe");
        }

        return result;
    }

    @Nullable
    public String getRefmtPath() {
        String result;

        String bsLocation = getBsPlatformLocation();
        Project project = m_module.getProject();

        result = getRefmtBin(project, bsLocation + "/lib");
        if (result == null) {
            result = getRefmtBin(project, bsLocation + "/bin");
        }

        return result;
    }

    @Nullable
    private String getRefmtBin(@NotNull Project project, @NotNull String root) {
        String binary = Platform.getBinaryPath(project, root + "/refmt3.exe");
        if (binary == null) {
            binary = Platform.getBinaryPath(project, root + "/refmt.exe");
        }
        return binary;
    }

    @NotNull
    private String getBsPlatformLocation() {
        BsFacetConfiguration configuration = BsFacet.getConfiguration(m_module);
        String bsbLocation = configuration == null ? "" : configuration.location.replace('\\', '/');
        if (bsbLocation.isEmpty()) {
            bsbLocation = "node_modules/bs-platform";
        }
        return bsbLocation;
    }

    @Nullable
    public String getBasePath() {
        return m_module.getProject().getBaseDir().getCanonicalPath();
    }

    @Nullable
    public String getBscPath() {
        String bsbPath = getBsbPath();
        return bsbPath == null ? null : bsbPath.replace("bsb.exe", "bsc.exe");
    }
}
