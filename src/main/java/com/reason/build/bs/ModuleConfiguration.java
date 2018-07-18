package com.reason.build.bs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
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

    @NotNull
    public Project getProject() {
        return m_project;
    }

    @Nullable
    public String getBsbPath(@NotNull VirtualFile sourceFile) {
        String bsLocation = getBsPlatformLocation();

        String result = Platform.getBinaryPath(m_project, sourceFile, bsLocation + "/lib/bsb.exe");
        if (result == null) {
            result = Platform.getBinaryPath(m_project, sourceFile, bsLocation + "/bin/bsb.exe");
        }

        return result;
    }

    @Nullable
    public String getBscPath(@NotNull VirtualFile sourceFile) {
        String bsbPath = getBsbPath(sourceFile);
        return bsbPath == null ? null : bsbPath.replace("bsb.exe", "bsc.exe");
    }

    @Nullable
    public String getRefmtPath(@NotNull VirtualFile sourceFile) {
        String bsLocation = getBsPlatformLocation();

        String result = getRefmtBin(m_project, sourceFile, bsLocation + "/lib");
        if (result == null) {
            result = getRefmtBin(m_project, sourceFile, bsLocation + "/bin");
        }

        return result;
    }

    public boolean isOnSaveEnabled() {
        ReasonSettings settings = ReasonSettings.getInstance(m_project);
        return settings != null && settings.refmtOnSave;
    }

    @Nullable
    private String getRefmtBin(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull String root) {
        String binary = Platform.getBinaryPath(project, sourceFile, root + "/refmt3.exe");
        if (binary == null) {
            binary = Platform.getBinaryPath(project, sourceFile, root + "/refmt.exe");
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
    public String getWorkingDir(@NotNull VirtualFile sourceFile) {
        ReasonSettings settings = ReasonSettings.getInstance(m_project);
        String dir = settings == null ? getBasePath(sourceFile) : settings.workingDir;
        return dir != null && !dir.isEmpty() ? dir : getBasePath(sourceFile);
    }

    @NotNull
    public String getBasePath(@NotNull VirtualFile sourceFile) {
        VirtualFile rootDir = Platform.findBaseRootFromFile(m_project, sourceFile);
        String canonicalPath = rootDir.getCanonicalPath();
        assert canonicalPath != null;
        return canonicalPath;
    }
}
