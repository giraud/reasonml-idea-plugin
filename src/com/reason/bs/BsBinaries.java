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
		String workingDir = FILE_PROTOCOL_PREFIX + ReasonSettings.getInstance(project).getWorkingDir(sourceFile);

		VirtualFile bsbPath = getPathToBinary(virtualFileManager, "bsb", workingDir + LOCAL_BS_PLATFORM);
		if (bsbPath == null) {
			bsbPath = getPathToBinary(virtualFileManager, "bsb", workingDir + LOCAL_NODE_MODULES_BIN);
		}
		return bsbPath == null ? null : bsbPath.getPath();
	}

	@Nullable
	public static String getBscPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
		VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
		String workingDir = FILE_PROTOCOL_PREFIX + ReasonSettings.getInstance(project).getWorkingDir(sourceFile);

		VirtualFile bscPath = getPathToBinary(virtualFileManager, "bsc", workingDir + LOCAL_BS_PLATFORM);
		if (bscPath == null) {
			bscPath = getPathToBinary(virtualFileManager, "bsc", workingDir + LOCAL_NODE_MODULES_BIN);
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

	@Nullable
	private static VirtualFile getPathToBinary(VirtualFileManager virtualFileManager, String target, String workingDir) {
		String platform = getOsBsPrefix();

		// First, try 7.2 locations
		VirtualFile bin = virtualFileManager.findFileByUrl(workingDir + "/" + platform + "/" + target + ".exe");
		if (bin == null) {
			// Try old locations
			bin = virtualFileManager.findFileByUrl(workingDir + "/lib/" + target + ".exe");
		}

		if (bin == null) {
			bin = virtualFileManager.findFileByUrl(workingDir + "/" + target + (SystemInfo.isWindows ? ".cmd" : ""));
			if (bin != null) {
				VirtualFile canonicalFile = bin.getCanonicalFile();
				if (canonicalFile != null) {
					VirtualFile bsDir = canonicalFile.getParent();
					VirtualFile resolvedBin = virtualFileManager.findFileByUrl(bsDir + "/" + platform + "/" + target + ".exe");
					if (resolvedBin == null) {
                        resolvedBin = virtualFileManager.findFileByUrl(bsDir + "/lib/" + target + ".exe");
					}
					if (resolvedBin != null) {
					    bin = resolvedBin;
                    }
				}
			}
		}

		return bin;
	}
}
