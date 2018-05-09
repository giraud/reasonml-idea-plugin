package com.reason.bs;

import com.intellij.openapi.project.Project;
import com.reason.Platform;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleConfiguration {

    private static final String LOCAL_BS_PLATFORM = "node_modules/bs-platform";

    @NotNull
    private final Project m_project;

    public ModuleConfiguration(@NotNull Project project) {
        m_project = project;
    }

    @Nullable
    public String getBsbPath() {
        String bsLocation = getBsPlatformLocation();

        String result = Platform.getBinaryPath(m_project, bsLocation + "/lib/bsb.exe");
        if (result == null) {
            result = Platform.getBinaryPath(m_project, bsLocation + "/bin/bsb.exe");
        }

        return result;
    }

    @Nullable
    public String getBscPath() {
        String bsbPath = getBsbPath();
        return bsbPath == null ? null : bsbPath.replace("bsb.exe", "bsc.exe");
    }

    @Nullable
    public String getRefmtPath() {
        String bsLocation = getBsPlatformLocation();

        String result = getRefmtBin(m_project, bsLocation + "/lib");
        if (result == null) {
            result = getRefmtBin(m_project, bsLocation + "/bin");
        }

        return result;
    }

    public boolean isOnSaveEnabled() {
        ReasonSettings settings = ReasonSettings.getInstance(m_project);
        return settings != null && settings.refmtOnSave;
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
        ReasonSettings settings = ReasonSettings.getInstance(m_project);
        String bsbLocation = settings == null ? "" : settings.location.replace('\\', '/');
        if (bsbLocation.isEmpty()) {
            bsbLocation = LOCAL_BS_PLATFORM;
        }
        return bsbLocation;
    }

    @NotNull
    public String getRefmtWidth() {
        ReasonSettings settings = ReasonSettings.getInstance(m_project);
        return settings == null ? "80" : settings.refmtWidth;
    }

    @NotNull
    public String getWorkingDir() {
        ReasonSettings settings = ReasonSettings.getInstance(m_project);
        String dir = settings == null ? getBasePath() : settings.workingDir;
        return dir != null && !dir.isEmpty() ? dir : getBasePath();
    }

    @NotNull
    public String getBasePath() {
        String canonicalPath = m_project.getBaseDir().getCanonicalPath();
        assert canonicalPath != null;
        return canonicalPath;
    }
}
