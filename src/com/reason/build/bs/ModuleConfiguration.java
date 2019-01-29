package com.reason.build.bs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleConfiguration {

    private static final String LOCAL_BS_PLATFORM = "node_modules/bs-platform";

    private ModuleConfiguration() {
    }

    @Nullable
    public static String getBsbPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String bsLocation = getBsPlatformLocation(project);

        String result = Platform.getBinaryPath(project, sourceFile, bsLocation + "/lib/bsb.exe");
        if (result == null) {
            result = Platform.getBinaryPath(project, sourceFile, bsLocation + "/bin/bsb.exe");
        }

        return result;
    }

    @Nullable
    public static String getBscPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String bsbPath = getBsbPath(project, sourceFile);
        return bsbPath == null ? null : bsbPath.replace("bsb.exe", "bsc.exe");
    }

    @Nullable
    public static String getRefmtPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String bsLocation = getBsPlatformLocation(project);

        String result = getRefmtBin(project, sourceFile, bsLocation + "/lib");
        if (result == null) {
            result = getRefmtBin(project, sourceFile, bsLocation + "/bin");
        }

        return result;
    }

    public static boolean isOnSaveEnabled(@NotNull Project project) {
        ReasonSettings reasonSettings = ReasonSettings.getInstance(project);
        return reasonSettings.enabled && reasonSettings.refmtOnSave;
    }

    @Nullable
    private static String getRefmtBin(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull String root) {
        String binary = Platform.getBinaryPath(project, sourceFile, root + "/refmt3.exe");
        if (binary == null) {
            binary = Platform.getBinaryPath(project, sourceFile, root + "/refmt.exe");
        }
        return binary;
    }

    @NotNull
    private static String getBsPlatformLocation(@NotNull Project project) {
        ReasonSettings settings = ReasonSettings.getInstance(project);
        String bsbLocation = settings == null ? "" : settings.location.replace('\\', '/');
        if (bsbLocation.isEmpty()) {
            bsbLocation = LOCAL_BS_PLATFORM;
        }
        return bsbLocation;
    }

    @NotNull
    public static String getRefmtWidth(@NotNull Project project) {
        return ReasonSettings.getInstance(project).refmtWidth;
    }

    @NotNull
    public static String getWorkingDir(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String workingDir = ReasonSettings.getInstance(project).workingDir;
        return workingDir.isEmpty() ? getBasePath(project, sourceFile) : workingDir;
    }

    @NotNull
    public static String getBasePath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFile rootDir = Platform.findBaseRootFromFile(project, sourceFile);
        String canonicalPath = rootDir.getCanonicalPath();
        assert canonicalPath != null;
        return canonicalPath;
    }
}
