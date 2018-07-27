package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import org.jetbrains.annotations.Nullable;

public class FileHelper {
    private FileHelper() {
    }

    public static boolean isCompilable(@Nullable FileType fileType) {
        return isReason(fileType) || isOCaml(fileType);
    }

    public static boolean isReason(@Nullable FileType fileType) {
        return fileType instanceof RmlFileType || fileType instanceof RmlInterfaceFileType;
    }

    public static boolean isOCaml(@Nullable FileType fileType) {
        return fileType instanceof OclFileType || fileType instanceof OclInterfaceFileType;
    }

    public static boolean isInterface(@Nullable FileType fileType) {
        return fileType instanceof RmlInterfaceFileType || fileType instanceof OclInterfaceFileType;
    }
}
