package com.reason.bs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.vfs.StandardFileSystems.FILE_PROTOCOL_PREFIX;
import static com.reason.Platform.LOCAL_BS_PLATFORM;
import static com.reason.Platform.LOCAL_NODE_MODULES_BIN;

public class BsBinaries {

    @Nullable
    public static String getBsbPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        String platform = getOsBsPrefix();
        String workingDir = FILE_PROTOCOL_PREFIX + ReasonSettings.getInstance(project).getWorkingDir(sourceFile);

        // First, try 7.2 locations
        VirtualFile bsbPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_BS_PLATFORM + "/" + platform + "/bsb.exe");
        if (bsbPath == null) {
            // Try old locations
            bsbPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_BS_PLATFORM + "/lib/bsb.exe");
            if (bsbPath == null) {
                bsbPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_NODE_MODULES_BIN + "/bsb" + (SystemInfo.isWindows ? ".cmd" : ""));
            }
        }

        return bsbPath == null ? null : bsbPath.getPath();
    }

    // Duplicate BsbPath for more safety
    @Nullable
    public static String getBscPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        String platform = getOsBsPrefix();
        String workingDir = FILE_PROTOCOL_PREFIX + ReasonSettings.getInstance(project).getWorkingDir(sourceFile);

        // First, try 7.2 locations
        VirtualFile bscPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_BS_PLATFORM + "/" + platform + "/bsc.exe");
        if (bscPath == null) {
            // Try old locations
            bscPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_BS_PLATFORM + "/lib/bsc.exe");
            if (bscPath == null) {
                bscPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_NODE_MODULES_BIN + "/bsc" + (SystemInfo.isWindows ? ".cmd" : ""));
            }
        }

        return bscPath == null ? null : bscPath.getPath();
    }

    @Nullable
    public static String getRefmtPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        String platform = getOsBsPrefix();
        String workingDir = FILE_PROTOCOL_PREFIX + ReasonSettings.getInstance(project).getWorkingDir(sourceFile);

        // First, try 7.2 locations
        VirtualFile refmtPath = virtualFileManager.findFileByUrl(workingDir + LOCAL_BS_PLATFORM + "/" + platform + "/refmt.exe");
        if (refmtPath == null) {
            // Try oler locations
            refmtPath = getRefmtBin(workingDir + LOCAL_BS_PLATFORM + "/lib");
            if (refmtPath == null) {
                refmtPath = getRefmtBin(workingDir + LOCAL_BS_PLATFORM + "/bin");
                if (refmtPath == null) {
                    refmtPath = getRefmtBin(workingDir + LOCAL_NODE_MODULES_BIN);
                }
            }
        }

        return refmtPath == null ? null : refmtPath.getPath();
    }

    @Nullable
    private static VirtualFile getRefmtBin(@NotNull String path) {
        VirtualFileManager vfManager = VirtualFileManager.getInstance();

        VirtualFile binary = vfManager.findFileByUrl(path + "/refmt.exe");

        if (binary == null) {
            binary = vfManager.findFileByUrl(path + "/refmt3.exe");
            if (binary == null) {
                binary = vfManager.findFileByUrl(path + "/bsrefmt" + (SystemInfo.isWindows ? ".cmd" : ""));
            }
        }

        return binary;
    }

    @NotNull
    private static String getOsBsPrefix() {
        if (SystemInfo.isWindows) {
            return "win32";
        }

        if (SystemInfo.isLinux) {
            return "linux";
        }

        if (SystemInfo.isMac) {
            return "darwin";
        }

        return "";
    }
}
